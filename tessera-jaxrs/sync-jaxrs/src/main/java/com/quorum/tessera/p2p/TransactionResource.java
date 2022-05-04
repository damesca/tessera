package com.quorum.tessera.p2p;

import static jakarta.ws.rs.core.MediaType.*;
import static java.util.Collections.emptyList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.quorum.tessera.base64.Base64Codec;
import com.quorum.tessera.data.MessageHash;
import com.quorum.tessera.enclave.EncodedPayloadCodec;
import com.quorum.tessera.enclave.PayloadEncoder;
import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.p2p.extendedPrivacy.ExtendedPrivacyRequest;
import com.quorum.tessera.p2p.extendedPrivacy.PsiRequest;
import com.quorum.tessera.p2p.extendedPrivacy.PsiResponse;
import com.quorum.tessera.p2p.recovery.ResendBatchRequest;
import com.quorum.tessera.p2p.resend.ResendRequest;
import com.quorum.tessera.recovery.resend.ResendBatchResponse;
import com.quorum.tessera.recovery.workflow.BatchResendManager;
import com.quorum.tessera.recovery.workflow.LegacyResendManager;
import com.quorum.tessera.shared.Constants;
import com.quorum.tessera.transaction.TransactionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides endpoints for dealing with transactions, including:
 *
 * <p>- creating new transactions and distributing them - deleting transactions - fetching
 * transactions - resending old transactions
 */
@Tag(name = "peer-to-peer")
@Path("/")
public class TransactionResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionResource.class);

  private final TransactionManager transactionManager;

  private final BatchResendManager batchResendManager;

  private final LegacyResendManager legacyResendManager;

  public TransactionResource(
      final TransactionManager transactionManager,
      final BatchResendManager batchResendManager,
      final LegacyResendManager legacyResendManager) {
    this.transactionManager = Objects.requireNonNull(transactionManager);
    this.batchResendManager = Objects.requireNonNull(batchResendManager);
    this.legacyResendManager = Objects.requireNonNull(legacyResendManager);
  }

  @Operation(
      summary = "/resend",
      operationId = "requestPayloadResend",
      description =
          "initiate resend of either an INDIVIDUAL transaction or ALL transactions involving a given public key")
  @ApiResponse(
      responseCode = "200",
      description = "resent payload",
      content =
          @Content(
              array =
                  @ArraySchema(
                      schema =
                          @Schema(
                              description =
                                  "empty if request was for ALL; else the encoded INDIVIDUAL transaction",
                              type = "string",
                              format = "byte"))))
  @POST
  @Path("resend")
  @Consumes(APPLICATION_JSON)
  @Produces(TEXT_PLAIN)
  public Response resend(@Valid @NotNull final ResendRequest resendRequest) {

    LOGGER.debug("Received resend request");

    PayloadEncoder payloadEncoder = PayloadEncoder.create(EncodedPayloadCodec.LEGACY);

    PublicKey recipient =
        Optional.of(resendRequest)
            .map(ResendRequest::getPublicKey)
            .map(Base64Codec.create()::decode)
            .map(PublicKey::from)
            .get();

    MessageHash transactionHash =
        Optional.of(resendRequest)
            .map(ResendRequest::getKey)
            .map(Base64.getDecoder()::decode)
            .map(MessageHash::new)
            .orElse(null);

    com.quorum.tessera.recovery.resend.ResendRequest request =
        com.quorum.tessera.recovery.resend.ResendRequest.Builder.create()
            .withType(
                com.quorum.tessera.recovery.resend.ResendRequest.ResendRequestType.valueOf(
                    resendRequest.getType()))
            .withRecipient(recipient)
            .withHash(transactionHash)
            .build();

    com.quorum.tessera.recovery.resend.ResendResponse response =
        legacyResendManager.resend(request);

    Response.ResponseBuilder builder = Response.ok();
    Optional.ofNullable(response.getPayload())
        .map(payloadEncoder::encode)
        .ifPresent(builder::entity);
    return builder.build();
  }

  @Operation(
      summary = "/resendBatch",
      operationId = "requestPayloadBatchResend",
      description = "initiate resend of all transactions for a given public key in batches")
  @ApiResponse(
      responseCode = "200",
      description = "count of total transactions being resent",
      content =
          @Content(
              schema =
                  @Schema(
                      implementation = com.quorum.tessera.p2p.recovery.ResendBatchResponse.class)))
  @POST
  @Path("resendBatch")
  @Consumes(APPLICATION_JSON)
  @Produces(APPLICATION_JSON)
  public Response resendBatch(@Valid @NotNull final ResendBatchRequest resendBatchRequest) {

    LOGGER.debug("Received resend request");

    com.quorum.tessera.recovery.resend.ResendBatchRequest request =
        com.quorum.tessera.recovery.resend.ResendBatchRequest.Builder.create()
            .withPublicKey(resendBatchRequest.getPublicKey())
            .withBatchSize(resendBatchRequest.getBatchSize())
            .build();

    ResendBatchResponse response = batchResendManager.resendBatch(request);

    com.quorum.tessera.p2p.recovery.ResendBatchResponse responseEntity =
        new com.quorum.tessera.p2p.recovery.ResendBatchResponse();
    responseEntity.setTotal(response.getTotal());

    Response.ResponseBuilder builder = Response.status(Response.Status.OK);
    builder.entity(responseEntity);
    return builder.build();
  }

  // path push is overloaded (RecoveryResource & TransactionResource); swagger cannot handle
  // situations like this so this operation documents both
  @Operation(
      summary = "/push",
      operationId = "pushPayload",
      description = "store encoded payload to the server's database")
  @ApiResponse(
      responseCode = "201",
      description = "hash of encoded payload",
      content =
          @Content(
              mediaType = TEXT_PLAIN,
              schema =
                  @Schema(
                      description = "hash of encrypted payload",
                      type = "string",
                      format = "base64")))
  @ApiResponse(
      responseCode = "403",
      description =
          "server is in recovery mode and encoded payload is not a Standard Private transaction")
  @POST
  @Path("push")
  @Consumes(APPLICATION_OCTET_STREAM)
  public Response push(
      @Schema(description = "encoded payload") final byte[] payload,
      @HeaderParam(Constants.API_VERSION_HEADER)
          @Parameter(
              description = "client's supported API versions",
              array = @ArraySchema(schema = @Schema(type = "string")))
          final List<String> headers) {

    LOGGER.debug("Received push request");

    final Set<String> versions =
        Optional.ofNullable(headers).orElse(emptyList()).stream()
            .filter(Objects::nonNull)
            .flatMap(v -> Arrays.stream(v.split(",")))
            .collect(Collectors.toSet());

    final EncodedPayloadCodec codec = EncodedPayloadCodec.getPreferredCodec(versions);

    final PayloadEncoder payloadEncoder = PayloadEncoder.create(codec);

    final MessageHash messageHash = transactionManager.storePayload(payloadEncoder.decode(payload));
    LOGGER.debug("Push request generated hash {}", messageHash);
    // TODO: Return the query url not the string of the messageHash
    return Response.status(Response.Status.CREATED).entity(Objects.toString(messageHash)).build();
  }

  @Operation(
      summary = "/extendedPrivacy",
      operationId = "extendedPrivacy",
      description = "perform extended privacy protocol")
  @ApiResponse(
      responseCode = "201",
      description = "protocol correctly performed",
      content =
          @Content(
              mediaType = TEXT_PLAIN,
              schema =
                  @Schema(
                      description = "hash of encrypted payload",
                      type = "string",
                      format = "base64")))
  @ApiResponse(
      responseCode = "403",
      description =
          "the protocol cannot be correctly executed")
    @POST
    @Path("extendedPrivacy")
    @Consumes(APPLICATION_JSON)
    public Response extendedPrivacy(final ExtendedPrivacyRequest request) {

        /*LOG*/System.out.println(">>> [TransactionResource] extendedPrivacy()");

        // Receive the /extendedPrivacy request from another Tessera node
        String protocolId = request.getProtocolId();
        Integer port = request.getPort();
        String pmt = request.getPmt();
        
        // TODO: Connect to socket and perform the private protocol (PSI)
        // ...
        /*LOG*/System.out.println(">>> [TransactionResource] PsiClient connecting to server...");
        String result = "0x0000000000000000000000000000000000000000000000000000000000000003";

        // TODO: Save result on database?

        return Response.status(Response.Status.CREATED).entity(pmt).build();
    }

    @POST
    @Path("privateSetIntersection")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response privateSetIntersection(final PsiRequest request) {
        /*LOG*/System.out.println(">>> [TransactionResource] privateSetIntersection");
        //byte[] messages = request.getMessages();
        //byte[] messages = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        //   0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        //   0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x06};

        // DONE: I need to handle my privateArgs here, to perform PSI
        byte[] privateArgs = transactionManager.retrievePrivateArguments(request.getKey());
        byte[] otherPrivateArgs = request.getMessages();

        byte[] result = localIntersection(privateArgs, otherPrivateArgs);

        com.quorum.tessera.p2p.extendedPrivacy.PsiResponse responseEntity =
            new com.quorum.tessera.p2p.extendedPrivacy.PsiResponse();
        responseEntity.setMessages(/*privateArgs*/ result);

        Response.ResponseBuilder builder = Response.status(Response.Status.OK);
        builder.entity(responseEntity);
        return builder.build();
    }

    private byte[] localIntersection(final byte[] a, final byte[] b) {

        int stdLength = 64;

        List<byte[]> aItems = new ArrayList<>();
        List<byte[]> bItems = new ArrayList<>();
        List<byte[]> result = new ArrayList<>();
        
        for(int i = 0; i < a.length; i = i + stdLength) {
            byte[] tmp = Arrays.copyOfRange(a, i, i + stdLength);
            aItems.add(tmp);
        }
        for(int i = 0; i < b.length; i = i + stdLength) {
            byte[] tmp = Arrays.copyOfRange(b, i, i + stdLength);
            bItems.add(tmp);
        }
        for(byte[] ia : aItems) {
            for(byte[] ib : bItems) {
                if(Arrays.equals(ia, ib)) {
                    result.add(ia);
                }
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            for(byte[] item : result) {
                outputStream.write(item);
            }
        }catch(IOException e) {
            /*LOG*/System.out.println(e);
        }

        return outputStream.toByteArray();
    }
}
