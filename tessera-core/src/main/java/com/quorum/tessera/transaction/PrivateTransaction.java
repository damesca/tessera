package com.quorum.tessera.transaction;

import org.apache.tuweni.bytes.Bytes;

import static com.google.common.base.Preconditions.checkState;
//import static org.hyperledger.besu.crypto.Hash.keccak256;
//import static org.hyperledger.besu.plugin.data.Restriction.RESTRICTED;
//import static org.hyperledger.besu.plugin.data.Restriction.UNRESTRICTED;
//import static org.hyperledger.besu.plugin.data.Restriction.UNSUPPORTED;

//import org.hyperledger.besu.crypto.KeyPair;
//import org.hyperledger.besu.crypto.SECPPublicKey;
//import org.hyperledger.besu.crypto.SECPSignature;
//import org.hyperledger.besu.crypto.SignatureAlgorithm;
//import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
//import org.hyperledger.besu.datatypes.Address;
//import org.hyperledger.besu.datatypes.Hash;
//import org.hyperledger.besu.datatypes.Wei;
//import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
//import org.hyperledger.besu.ethereum.rlp.RLP;
//import org.hyperledger.besu.ethereum.rlp.RLPException;
//import org.hyperledger.besu.ethereum.rlp.RLPInput;
//import org.hyperledger.besu.ethereum.rlp.RLPOutput;
//import org.hyperledger.besu.plugin.data.Restriction;
//import org.identityconnectors.common.logging.Log;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.hyperledger.besu.ethereum.rlp.RLP;
import org.hyperledger.besu.ethereum.rlp.RLPInput;

public class PrivateTransaction {
    public static void bytes(){
        byte[] raw = {(byte)0x1};
        Bytes a = Bytes.wrap(raw);
        System.out.println(a);

        UInt256 b = UInt256.fromBytes(a);
        System.out.println(b);

        List<Bytes> l = Lists.newArrayList();
        RLPInput input = RLP.input(a);
        byte x = input.readByte();
        System.out.println(x);
    }
}
