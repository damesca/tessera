package com.quorum.tessera.q2t;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import com.quorum.tessera.api.*;
import com.quorum.tessera.api.constraint.PrivacyValid;
import com.quorum.tessera.data.MessageHash;
import com.quorum.tessera.enclave.PrivacyGroup;
import com.quorum.tessera.enclave.PrivacyMode;
import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.privacygroup.PrivacyGroupManager;
import com.quorum.tessera.transaction.TransactionManager;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hyperledger.besu.ethereum.privacy.PrivateTransaction;
import org.hyperledger.besu.ethereum.rlp.RLP;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
import org.apache.tuweni.bytes.Bytes;
import extended.privacy.PrivateDataExtractor;
import extended.privacy.ObliviousTransferServer;
import extended.privacy.ObliviousTransferClient;
import java.util.Arrays;
import java.util.Random;
import extended.privacy.ContractAddress;
import org.hyperledger.besu.datatypes.Address;
import org.apache.tuweni.bytes.DelegatingBytes;

@Tag(name = "quorum-to-tessera")
@Path("/")
public class BesuTransactionResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionResource.class);

  private static final String ORION = "application/vnd.orion.v1+json";

  private final TransactionManager transactionManager;

  private final PrivacyGroupManager privacyGroupManager;

  private final Base64.Decoder base64Decoder = Base64.getDecoder();

  private final Base64.Encoder base64Encoder = Base64.getEncoder();

  public BesuTransactionResource(
      TransactionManager transactionManager, PrivacyGroupManager privacyGroupManager) {
    this.transactionManager = Objects.requireNonNull(transactionManager);
    this.privacyGroupManager = Objects.requireNonNull(privacyGroupManager);
  }

  @Hidden
  @POST
  @Path("send")
  @Consumes(APPLICATION_JSON)
  @Produces(APPLICATION_JSON)
  public Response send(@NotNull @Valid @PrivacyValid final SendRequest sendRequest) {

    final PublicKey sender =
        Optional.ofNullable(sendRequest.getFrom())
            .map(base64Decoder::decode)
            .map(PublicKey::from)
            .orElseGet(transactionManager::defaultPublicKey);

    final Optional<PrivacyGroup.Id> optionalPrivacyGroup =
        Optional.ofNullable(sendRequest.getPrivacyGroupId()).map(PrivacyGroup.Id::fromBase64String);

    final List<PublicKey> recipientList =
        optionalPrivacyGroup
            .map(privacyGroupManager::retrievePrivacyGroup)
            .map(PrivacyGroup::getMembers)
            .orElse(
                Stream.of(sendRequest)
                    .filter(sr -> Objects.nonNull(sr.getTo()))
                    .flatMap(s -> Stream.of(s.getTo()))
                    .map(base64Decoder::decode)
                    .map(PublicKey::from)
                    .collect(Collectors.toList()));

    final Set<MessageHash> affectedTransactions =
        Stream.ofNullable(sendRequest.getAffectedContractTransactions())
            .flatMap(Arrays::stream)
            .map(base64Decoder::decode)
            .map(MessageHash::new)
            .collect(Collectors.toSet());

    final byte[] execHash =
        Optional.ofNullable(sendRequest.getExecHash()).map(String::getBytes).orElse(new byte[0]);

    final PrivacyMode privacyMode = PrivacyMode.fromFlag(sendRequest.getPrivacyFlag());
    
    // Get the privateTransaction to select the subsequent procedures
    final byte[] rawPayload = base64Decoder.decode(sendRequest.getPayload());
    
    final PrivateTransaction privateTransaction =
          PrivateTransaction.readFrom(RLP.input(Bytes.wrap(rawPayload)));
    System.out.println(privateTransaction.toString());

    byte[] sendRequestPayload = null;
    int listeningPort = 0;
    String clientResponse = "";
    // Check otWith
    if(privateTransaction.getOtWith().compareTo(Bytes.ofUnsignedShort(0)) == 0) {
        // It is not a otWith transaction
        sendRequestPayload = sendRequest.getPayload();
    } else {
        // It is a otWith transaction
        if(privateTransaction.isContractCreation()) {
            // It is a contract creation

            //PrivateDataExtractor dataExtractor = PrivateDataExtractor.extractArguments(privateTransaction);
            //PrivateTransaction blindedTx = dataExtractor.getBlindedTransaction();
            //Bytes privateArguments = dataExtractor.getPrivateArguments();
            Bytes privateArguments = privateTransaction.getPrivateArgs();
            /*LOG*/System.out.println(privateArguments.toHexString());


            Random rndGenerator = new Random();
            listeningPort = rndGenerator.nextInt(1000)+6000; // Extract a port from 6000 to (6000+1000)

            System.out.println("Executing OTServer...");
            ObliviousTransferServer server = new ObliviousTransferServer(listeningPort, privateArguments);
            //listeningPort = server.getListeningPort();
            Thread t = new Thread(server);
            t.start();
            

            // DONE: send the endPoint port to the destination Tessera node (using requestBuilder)

            BytesValueRLPOutput rlpOutput = new BytesValueRLPOutput();
            privateTransaction.writeTo(rlpOutput);
            String stringBlindedPayload = rlpOutput.encoded().toBase64String();
            //sendRequestPayload = rlpOutput.encoded().toBase64String().getBytes();

            // The original Transaction is sent to the TxManager because it handles now the private data extraction
            sendRequestPayload = sendRequest.getPayload();
        } else {
            // It is not a contract creation

            // TODO: check if it is a "slice" call or not

            sendRequestPayload = sendRequest.getPayload();

            // DONE: connect to a listener point
            // DONE: get the listeningPort dynamically

            Bytes privateArguments = privateTransaction.getPrivateArgs();

            int port = 0;
            Optional<Address> contractAddress = privateTransaction.getTo();
            if(contractAddress.isPresent()){
                /*LOG*/System.out.println(" >>> [BesuTransactionResource] get transaction endPoint using contract address");
                /*LOG*/System.out.println(contractAddress.get().toHexString());
                port = this.transactionManager.getEndpoint(contractAddress.get());
            }else{
                /*LOG*/System.out.println(" >>> [BesuTransactionResource] contractAddress is not present");
            }
            
            /*LOG*/System.out.printf(" >> [BesuTransactionResource] retrievedPort: %d\n", port);

            System.out.println("Executing OTClient...");
            ObliviousTransferClient client = new ObliviousTransferClient();
            client.startConnection("127.0.0.1", port);
            clientResponse = client.sendMessage(privateArguments.toHexString());
            //System.out.println(clientResponse.substring(clientResponse.length()-2*64, clientResponse.length()));
            //clientResponse = clientResponse.substring(clientResponse.length()-2*64, clientResponse.length());
            client.stopConnection();
        }
    }

    //////////////////////////////

    final com.quorum.tessera.transaction.SendRequest.Builder requestBuilder =
        com.quorum.tessera.transaction.SendRequest.Builder.create()
            .withRecipients(recipientList)
            .withSender(sender)
            .withPayload(sendRequestPayload)
            .withExecHash(execHash)
            .withPrivacyMode(privacyMode)
            .withAffectedContractTransactions(affectedTransactions);

    if(listeningPort != 0){
        requestBuilder.withListeningPort(listeningPort);
    }

    optionalPrivacyGroup.ifPresentOrElse(
        requestBuilder::withPrivacyGroupId,
        () -> {
          PrivacyGroup legacyGroup =
              privacyGroupManager.createLegacyPrivacyGroup(sender, recipientList);
          requestBuilder.withPrivacyGroupId(legacyGroup.getId());
        });
    
    com.quorum.tessera.transaction.SendResponse response = null;
    if(privateTransaction.getOtWith().compareTo(Bytes.ofUnsignedShort(0)) == 0) {
        // It is not a otWith transaction
        response =
            transactionManager.send(requestBuilder.build());
    } else {
        if(privateTransaction.isContractCreation()) {
            Address contractAddress = ContractAddress.privateContractAddress(
                privateTransaction.getSender(),
                privateTransaction.getNonce(),
                privateTransaction.determinePrivacyGroupId()
            );
            /*LOG*/System.out.println("## [ContractAddress] ##");
            /*LOG*/System.out.println(contractAddress);
            requestBuilder.withContractAddress(contractAddress);
            response =
                transactionManager.sendPrivate(requestBuilder.build());
        }else{
            response =
                transactionManager.send(requestBuilder.build());
        }
    }
    /*
    final com.quorum.tessera.transaction.SendResponse response =
        transactionManager.send(requestBuilder.build());
    */

    final String encodedKey =
        Optional.of(response)
            .map(com.quorum.tessera.transaction.SendResponse::getTransactionHash)
            .map(MessageHash::getHashBytes)
            .map(base64Encoder::encodeToString)
            .get();
    /*LOG*/System.out.println(" >>> [BesuTransactionResource] SendResponse");
    /*LOG*/System.out.println(encodedKey);

    // Save (encodedKey, privateOutput) in database
    if((privateTransaction.getOtWith().compareTo(Bytes.ofUnsignedShort(0)) != 0) && (!privateTransaction.isContractCreation())){
        /*LOG*/System.out.println(" >>> [BesuTransactionResource] save(encodedKey, privateOutput)");
        transactionManager.storePrivateOutput(encodedKey, clientResponse);
    }

    final SendResponse sendResponse =
        Optional.of(response)
            .map(com.quorum.tessera.transaction.SendResponse::getTransactionHash)
            .map(MessageHash::getHashBytes)
            .map(base64Encoder::encodeToString)
            .map(messageHash -> new SendResponse(messageHash, null, null))
            .get();

    final URI location =
        UriBuilder.fromPath("transaction")
            .path(URLEncoder.encode(encodedKey, StandardCharsets.UTF_8))
            .build();

    return Response.status(Response.Status.OK)
        .type(APPLICATION_JSON)
        .location(location)
        .entity(sendResponse)
        .build();
  }

  @Operation(
      summary = "/receive",
      operationId = "getDecryptedPayloadJson",
      description =
          "get payload from database, decrypt, and return. This endpoint is only to be used by Besu")
  @ApiResponse(
      responseCode = "200",
      description = "decrypted payload",
      content = {
        @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = BesuReceiveResponse.class)),
        @Content(mediaType = ORION, schema = @Schema(implementation = BesuReceiveResponse.class))
      })
  @POST
  @Path("/receive")
  @Consumes({APPLICATION_JSON, ORION})
  @Produces(APPLICATION_JSON)
  public Response receive(@Valid final ReceiveRequest request) {

    LOGGER.debug("Received receive request");

    MessageHash transactionHash =
        Optional.of(request)
            .map(ReceiveRequest::getKey)
            .map(base64Decoder::decode)
            .map(MessageHash::new)
            .get();
    
    /*LOG*/System.out.println(" >>> [BesuTransactionResource] ReceiveRequest");
    /*LOG*/System.out.println(transactionHash.toString());

    PublicKey recipient =
        Optional.of(request)
            .map(ReceiveRequest::getTo)
            .filter(Predicate.not(String::isEmpty))
            .filter(Objects::nonNull)
            .map(base64Decoder::decode)
            .map(PublicKey::from)
            .orElse(null);

    com.quorum.tessera.transaction.ReceiveRequest receiveRequest =
        com.quorum.tessera.transaction.ReceiveRequest.Builder.create()
            .withTransactionHash(transactionHash)
            .withRecipient(recipient)
            .withRaw(request.isRaw())
            .build();

    com.quorum.tessera.transaction.ReceiveResponse response =
        transactionManager.receive(receiveRequest);

    ///*LOG*/System.out.println(Bytes.wrap(response.getUnencryptedTransactionData()).fromBase64String());

    // TODO: retrieve PrivateOutput from database based on transactionHash (if it exists)
    // TODO: insert PrivateOutput somewhere inside BesuReceiveResponse
    String receivedOutput = transactionManager.getPrivateOutput(transactionHash.toString());
    /*LOG*/System.out.println(" >>> [BesuTransactionResource] receivedOutput");
    /*LOG*/System.out.println(receivedOutput);

    BesuReceiveResponse receiveResponse = new BesuReceiveResponse();
    receiveResponse.setPayload(response.getUnencryptedTransactionData());
    receiveResponse.setSenderKey(response.sender().encodeToBase64());
    receiveResponse.setPrivateOutput(receivedOutput);
    response
        .getPrivacyGroupId()
        .map(PrivacyGroup.Id::getBase64)
        .ifPresent(receiveResponse::setPrivacyGroupId);

    //////// DECODE PAYLOAD AND INSPECT
    ///*LOG*/System.out.println(" >>> [BesuTransactionResource] result");
    ///*LOG*/System.out.println(Bytes.wrap(base64Decoder.decode(response.getUnencryptedTransactionData())).toHexString());
    ///////

    return Response.status(Response.Status.OK)
        .type(APPLICATION_JSON)
        .entity(receiveResponse)
        .build();
  }
}
