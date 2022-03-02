module tessera.crypto {

    requires com.google.common;
    requires org.bouncycastle.provider;
    requires tessera.plugin;

    exports org.hyperledger.besu.crypto;
}
