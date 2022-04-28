package com.quorum.tessera.q2t.internal;

import com.quorum.tessera.discovery.Discovery;
import com.quorum.tessera.enclave.EncodedPayload;
import com.quorum.tessera.enclave.EncodedPayloadCodec;
import com.quorum.tessera.enclave.PayloadEncoder;
import com.quorum.tessera.enclave.PrivacyMode;
import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.partyinfo.node.NodeInfo;
import com.quorum.tessera.transaction.exception.EnhancedPrivacyNotSupportedException;
import com.quorum.tessera.transaction.exception.MandatoryRecipientsNotSupportedException;
import com.quorum.tessera.transaction.publish.ExtendedPrivacyPublisher;
import com.quorum.tessera.transaction.publish.NodeOfflineException;
import com.quorum.tessera.transaction.publish.PayloadPublisher;
import com.quorum.tessera.transaction.publish.PublishPayloadException;
import com.quorum.tessera.version.EnhancedPrivacyVersion;
import com.quorum.tessera.version.MandatoryRecipientsVersion;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.quorum.tessera.q2t.internal.ExtendedPrivacyPayload;

public class ExtendedPrivacyPublisherImpl implements ExtendedPrivacyPublisher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedPrivacyPublisherImpl.class);

    private final Client client;
  
    private final Discovery discovery;
  
    public ExtendedPrivacyPublisherImpl(Client client, Discovery discovery) {
      this.client = Objects.requireNonNull(client);
      this.discovery = Objects.requireNonNull(discovery);
    }

    @Override
    public void publishExtendedPrivacy(
            byte[] protocolId, 
            int port, 
            byte[] pmt, 
            PublicKey recipientKey) {

        final NodeInfo remoteNodeInfo = discovery.getRemoteNodeInfo(recipientKey);

        final String targetUrl = remoteNodeInfo.getUrl();
        LOGGER.info("Publishing message to {}", targetUrl);

        ExtendedPrivacyPayload payload = new ExtendedPrivacyPayload();
        payload.setProtocolId(protocolId);
        payload.setPort(port);
        payload.setPmt(pmt);

        /*LOG*/System.out.println(">>> [ExtendedPrivacyPublisherImpl] payload built");
        /*LOG*/System.out.println(payload.getPort());
        /*LOG*/System.out.println(payload.getPmt());
        /*LOG*/System.out.println(payload.getProtocolId());

        JsonObject jsonPayload = Json.createObjectBuilder()
					.add("protocolId", new String(protocolId, StandardCharsets.UTF_8))
					.add("port", port)
					.add("pmt", new String(pmt, StandardCharsets.UTF_8))
					.build();

        Response response = 
            client
                .target(targetUrl)
                .path("/extendedPrivacy")
                .request()
                //.post(Entity.json(payload));
                .post(Entity.entity(jsonPayload, MediaType.APPLICATION_JSON_TYPE));
    }

}
