package com.quorum.tessera.enclave.rest;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EnclaveFindInvalidSecurityHashesRequestPayload implements Serializable {

  @XmlMimeType("base64Binary")
  private byte[] encodedPayload;

  private List<KeyValuePair> affectedContractTransactions;

  public byte[] getEncodedPayload() {
    return encodedPayload;
  }

  public void setEncodedPayload(byte[] encodedPayload) {
    this.encodedPayload = encodedPayload;
  }

  public List<KeyValuePair> getAffectedContractTransactions() {
    return affectedContractTransactions;
  }

  public void setAffectedContractTransactions(List<KeyValuePair> affectedContractTransactions) {
    this.affectedContractTransactions = affectedContractTransactions;
  }
}
