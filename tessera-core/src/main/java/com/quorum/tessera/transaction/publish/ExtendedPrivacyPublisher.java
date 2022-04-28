package com.quorum.tessera.transaction.publish;

import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.serviceloader.ServiceLoaderUtil;
import java.util.ServiceLoader;

/** Publishes messages from one node to another */
public interface ExtendedPrivacyPublisher {

  void publishExtendedPrivacy(byte[] protocolId, int port, byte[] pmt, PublicKey recipientKey);

  static ExtendedPrivacyPublisher create() {
    return ServiceLoaderUtil.loadSingle(ServiceLoader.load(ExtendedPrivacyPublisher.class));
  }
}
