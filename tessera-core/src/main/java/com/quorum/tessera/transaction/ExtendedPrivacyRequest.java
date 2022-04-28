package com.quorum.tessera.transaction;

import java.util.List;

import com.quorum.tessera.encryption.PublicKey;

public interface ExtendedPrivacyRequest {

    byte[] getProtocolId();
    Integer getPort();
    byte[] getPmt();
    List<PublicKey> getRecipientList();
    
    class Builder {

        private byte[] protocolId;
        private Integer port;
        private byte[] pmt;
        private List<PublicKey> recipientList;

        public Builder withProtocolId(byte[] protocolId) {
            this.protocolId = protocolId;
            return this;
        }

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withPmt(byte[] pmt) {
            this.pmt = pmt;
            return this;
        }

        public Builder withRecipientList(List<PublicKey> recipientList) {
            this.recipientList = recipientList;
            return this;
        }

        public static Builder create() {
            return new Builder() {};
        }

        public ExtendedPrivacyRequest build() {

            return new ExtendedPrivacyRequest() {
              @Override
              public byte[] getProtocolId() {
                return protocolId;
              }
              @Override
              public Integer getPort() {
                  return port;
              }
              @Override
              public byte[] getPmt() {
                  return pmt;
              }
              @Override
              public List<PublicKey> getRecipientList() {
                  return List.copyOf(recipientList);
              }
            };
        }

    }

}
