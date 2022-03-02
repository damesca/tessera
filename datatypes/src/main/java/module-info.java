module tessera.datatypes {

    requires com.google.common;
    requires tessera.crypto;
    requires tessera.rlp;
    requires com.fasterxml.jackson.annotation;
    requires tuweni.bytes;
    requires tessera.plugin;

    exports org.hyperledger.besu.datatypes;
}
