// TODO: unify modules from Besu into a new one
/**
 * module tessera.extended-privacy
 * 
 * package privacy
 * package crypto
 * package datatypes
 * package plugin-api
 * package rlp
 */
module tessera.privacy {

    requires tessera.crypto;
    requires tessera.datatypes;
    requires tessera.plugin;
    requires tessera.rlp;
    requires tuweni.bytes;
    requires tuweni.units;

    exports org.hyperledger.besu.ethereum.privacy;
}
