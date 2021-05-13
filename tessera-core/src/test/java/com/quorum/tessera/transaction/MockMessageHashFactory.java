package com.quorum.tessera.transaction;

import com.quorum.tessera.data.MessageHash;
import com.quorum.tessera.data.MessageHashFactory;

public class MockMessageHashFactory implements MessageHashFactory {

  @Override
  public MessageHash createFromCipherText(byte[] cipherText) {
    return new MessageHash(cipherText);
  }
}
