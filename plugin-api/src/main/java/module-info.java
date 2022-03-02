module tessera.plugin {

    requires tuweni.bytes;
    requires tuweni.units;
    
    exports org.hyperledger.besu.plugin.data;
    exports org.hyperledger.besu.plugin.services;
    exports org.hyperledger.besu.plugin.services.exception;
    exports org.hyperledger.besu.plugin.services.metrics;
    exports org.hyperledger.besu.plugin.services.permissioning;
    exports org.hyperledger.besu.plugin.services.privacy;
    exports org.hyperledger.besu.plugin.services.query;
    exports org.hyperledger.besu.plugin.services.rpc;
    exports org.hyperledger.besu.plugin.services.securitymodule;
    exports org.hyperledger.besu.plugin.services.securitymodule.data;
    exports org.hyperledger.besu.plugin.services.storage;
    
}
