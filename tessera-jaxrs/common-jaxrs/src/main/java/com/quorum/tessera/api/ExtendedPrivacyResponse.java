package com.quorum.tessera.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlMimeType;

public class ExtendedPrivacyResponse {
    
    @Schema(description = "result", type = "string")
    private byte[] result;

    public ExtendedPrivacyResponse() {}

    public byte[] getResult() {
        return this.result;
    }

    public void setResult(final byte[] result) {
        this.result = result;
    }

}
