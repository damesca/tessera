package com.quorum.tessera.enclave;

import com.quorum.tessera.encryption.PublicKey;
import java.util.*;

import org.hyperledger.besu.datatypes.Address;

public interface PrivacyMetadata {

  PrivacyMode getPrivacyMode();

  List<AffectedTransaction> getAffectedContractTransactions();

  byte[] getExecHash();

  Optional<PrivacyGroup.Id> getPrivacyGroupId();

  Set<PublicKey> getMandatoryRecipients();

  Optional<Integer> getListeningPort();

  Optional<Address> getContractAddress();

  class Builder {

    private PrivacyMode privacyMode;

    private List<AffectedTransaction> affectedTransactions = Collections.emptyList();

    private byte[] execHash = new byte[0];

    private PrivacyGroup.Id privacyGroupId;

    private Set<PublicKey> mandatoryRecipients = Collections.emptySet();

    private int listeningPort;

    private Address contractAddress;

    public static Builder create() {
      return new Builder();
    }

    public Builder withPrivacyMode(PrivacyMode privacyMode) {
      this.privacyMode = privacyMode;
      return this;
    }

    public Builder withAffectedTransactions(List<AffectedTransaction> affectedTransactions) {
      this.affectedTransactions = affectedTransactions;
      return this;
    }

    public Builder withExecHash(byte[] execHash) {
      this.execHash = execHash;
      return this;
    }

    public Builder withPrivacyGroupId(PrivacyGroup.Id privacyGroupId) {
      this.privacyGroupId = privacyGroupId;
      return this;
    }

    public Builder withMandatoryRecipients(Set<PublicKey> mandatoryRecipients) {
      this.mandatoryRecipients = mandatoryRecipients;
      return this;
    }

    public Builder withListeningPort(int listeningPort) {
      this.listeningPort = listeningPort;
      return this;
    }

    public Builder withContractAddress(Address contractAddress) {
      /*LOG*/System.out.println(" [PrivacyMetadata] withContractAddress()");
      this.contractAddress = contractAddress;
      return this;
    }

    public PrivacyMetadata build() {

      Objects.requireNonNull(privacyMode, "privacyMode is required");

      if ((privacyMode == PrivacyMode.PRIVATE_STATE_VALIDATION)
          == (execHash == null || execHash.length == 0)) {
        throw new RuntimeException("ExecutionHash data is invalid");
      }

      if ((privacyMode == PrivacyMode.MANDATORY_RECIPIENTS) == mandatoryRecipients.isEmpty()) {
        throw new RuntimeException(
            "Mandatory recipients data only applicable for Mandatory Recipients privacy mode. "
                + "In case no mandatory recipient is required, consider using Party Protection privacy mode");
      }

      return new PrivacyMetadata() {
        @Override
        public PrivacyMode getPrivacyMode() {
          return privacyMode;
        }

        @Override
        public List<AffectedTransaction> getAffectedContractTransactions() {
          return affectedTransactions;
        }

        @Override
        public byte[] getExecHash() {
          return execHash;
        }

        @Override
        public Optional<PrivacyGroup.Id> getPrivacyGroupId() {
          return Optional.ofNullable(privacyGroupId);
        }

        @Override
        public Set<PublicKey> getMandatoryRecipients() {
          return Set.copyOf(mandatoryRecipients);
        }

        @Override
        public Optional<Integer> getListeningPort() {
          return Optional.ofNullable(listeningPort);
        }

        @Override
        public Optional<Address> getContractAddress() {
          return Optional.ofNullable(contractAddress);
        }
      };
    }

    public static Builder forStandardPrivate() {
      return create().withPrivacyMode(PrivacyMode.STANDARD_PRIVATE);
    }
  }
}
