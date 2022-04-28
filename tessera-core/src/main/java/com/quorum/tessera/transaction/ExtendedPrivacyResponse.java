package com.quorum.tessera.transaction;

import java.util.Arrays;

public interface ExtendedPrivacyResponse {
    
    byte[] getPmt();

    class Builder {

        private byte[] pmt;

        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        public Builder withPmt(final byte[] pmt) {
            this.pmt = pmt;
            return this;
        }

        public ExtendedPrivacyResponse build() {
            
            return new ExtendedPrivacyResponse() {
                @Override
                public byte[] getPmt() {
                    return Arrays.copyOf(pmt, pmt.length);
                }
            };

        }

    }

}
