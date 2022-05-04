package com.quorum.tessera.api;

import io.swagger.v3.oas.annotations.media.Schema;

public class PsiResponse {

  @Schema(description = "psiResponse")
  private byte[] messages;

  public PsiResponse() {}

  public PsiResponse(byte[] messages) {
    this.messages = messages;
  }

  public byte[] getMessages() {
    return messages;
  }

  public void setMessages(byte[] messages) {
    this.messages = messages;
  }
}