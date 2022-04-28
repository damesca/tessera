package com.quorum.tessera.api;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlMimeType;

public class ExtendedPrivacyRequest {
    
    @Schema(description = "protocolId", type = "string")
    private byte[] protocolId;

    @Schema(description = "privateArgs", type = "string")
    private byte[] privateArgs;

    @Schema(description = "pmt", type = "string")
    private byte[] pmt;

    @Schema(description = "recipients", type = "string")
    private String[] recipients;

    public byte[] getProtocolId() {
        return this.protocolId;
    }

    public void setProtocolId(final byte[] protocolId) {
        this.protocolId = protocolId;
    }

    public byte[] getPrivateArgs() {
        return this.privateArgs;
    }

    public void setPrivateArgs(final byte[] privateArgs) {
        this.privateArgs = privateArgs;
    }

    public byte[] getPmt() {
        return this.pmt;
    }

    public void setPmt(final byte[] pmt) {
        this.pmt = pmt;
    }

    public String[] getRecipients() {
        return this.recipients;
    }

    public void setRecipients(final String[] recipients) {
        this.recipients = recipients;
    }

}
