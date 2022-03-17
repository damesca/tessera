package extended.privacy;

import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.ethereum.rlp.RLP;
import static org.hyperledger.besu.crypto.Hash.keccak256;
import org.apache.tuweni.bytes.Bytes;

public class ContractAddress {
    
    public static Address privateContractAddress(
      final Address senderAddress, final long nonce, final Bytes privacyGroupId) {
    return Address.extract(
        keccak256(
            RLP.encode(
                out -> {
                  out.startList();
                  out.writeBytes(senderAddress);
                  out.writeLongScalar(nonce);
                  out.writeBytes(privacyGroupId);
                  out.endList();
                })));
  }

}
