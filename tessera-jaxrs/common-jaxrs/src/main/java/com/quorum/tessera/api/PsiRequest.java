package com.quorum.tessera.api;

import io.swagger.v3.oas.annotations.media.Schema;

public class PsiRequest {
    
    @Schema(description = "messages", type = "string")
    private byte[] messages;

    @Schema(description = "key", type = "key")
    private byte[] key;

    public byte[] getMessages() {
        return this.messages;
    }

    public void setMessages(final byte[] messages) {
        this.messages = messages;
    }

    public byte[] getKey() {
        return this.key;
    }

    public void setKey(final byte[] key) {
        this.key = key;
    }

}