package com.quorum.tessera.q2t.internal;

import com.quorum.tessera.enclave.PrivacyMode;
//import jakarta.xml.bind.annotation.XmlMimeType;
//import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

public class ExtendedPrivacyPayload {

    private byte[] protocolId;

    private Integer port;

    private byte[] pmt;

    public byte[] getProtocolId() {
        return this.protocolId;
    }

    public void setProtocolId(final byte[] protocolId) {
        this.protocolId = protocolId;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public byte[] getPmt() {
        return this.pmt;
    }

    public void setPmt(final byte[] pmt) {
        this.pmt = pmt;
    }

}
