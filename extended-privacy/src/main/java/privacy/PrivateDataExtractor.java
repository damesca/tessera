package extended.privacy;

import org.hyperledger.besu.ethereum.privacy.PrivateTransaction;
import org.hyperledger.besu.ethereum.rlp.RLP;
import org.hyperledger.besu.ethereum.rlp.RLPInput;

import org.apache.tuweni.bytes.Bytes;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import extended.privacy.PrivateSetIntersectionParams;

public class PrivateDataExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(PrivateDataExtractor.class);

    private final PrivateTransaction originalTransaction;
    private final PrivateTransaction blindedTransaction;
    private final Bytes privateArguments;


    protected PrivateDataExtractor(
            PrivateTransaction originalTransaction,
            PrivateTransaction blindedTransaction,
            Bytes privateArguments) {
        this.originalTransaction = originalTransaction;
        this.blindedTransaction = blindedTransaction;
        this.privateArguments = privateArguments;
    }

    public static PrivateDataExtractor blindPrivateArguments(PrivateTransaction p) {
        Bytes args = p.getPrivateArgs();

        //ObliviousTransferParams otp = ObliviousTransferParams.readFrom(args);
        PrivateSetIntersectionParams params = PrivateSetIntersectionParams.readFrom(args);
        Bytes privateArguments = params.getNoInfoArgs();

        final PrivateTransaction.Builder builder = PrivateTransaction.builder()
            .gasPrice(p.getGasPrice())
            .gasLimit(p.getGasLimit())
            .nonce(p.getNonce())
            .value(p.getValue())
            .payload(p.getPayload())
            .sender(p.getSender())
            .signature(p.getSignature())
            .privateFrom(p.getPrivateFrom())
            .restriction(p.getRestriction())
            .otWith(p.getOtWith())
            .privateArgs(privateArguments)
            .extSignature(p.getExtSignature());

            
        p.getTo().ifPresent(builder::to);
        p.getChainId().ifPresent(builder::chainId);
        p.getPrivateFor().ifPresent(builder::privateFor);
        p.getPrivacyGroupId().ifPresent(builder::privacyGroupId);

        PrivateTransaction blindedTransaction = builder.build();
        
        return new PrivateDataExtractor(
            p,
            blindedTransaction,
            privateArguments
        );
    }

    // Legacy method, currently not in use
    public static PrivateDataExtractor extractArguments(PrivateTransaction p) {
        // TODO: better reading of the values using RLP
        Bytes payload = p.getPayload();
        int index = ((payload.toHexString()).lastIndexOf("0033"))/2+1;
        Bytes args = payload.slice(index);
        
        ObliviousTransferParams otp = ObliviousTransferParams.readFrom(args);
        //Bytes senderAddr = otp.getSenderAddr();
        //Bytes receiverAddr = otp.getReceiverAddr();
        //int numberOfItems = otp.getNumberOfItems();
        //List<Bytes> itemList = otp.getItems();
        Bytes privateArguments = otp.getNoInfoArgs();

        Bytes newPayload = Bytes.wrap(
            payload.slice(0,index),
            privateArguments
        );
        
        final PrivateTransaction.Builder builder = PrivateTransaction.builder()
            .gasPrice(p.getGasPrice())
            .gasLimit(p.getGasLimit())
            .nonce(p.getNonce())
            .value(p.getValue())
            .payload(newPayload)
            .sender(p.getSender())
            .signature(p.getSignature())
            .privateFrom(p.getPrivateFrom())
            .restriction(p.getRestriction())
            .otWith(p.getOtWith());
            
        p.getTo().ifPresent(builder::to);
        p.getChainId().ifPresent(builder::chainId);
        p.getPrivateFor().ifPresent(builder::privateFor);
        p.getPrivacyGroupId().ifPresent(builder::privacyGroupId);

        PrivateTransaction blindedTransaction = builder.build();
        
        return new PrivateDataExtractor(
            p,
            blindedTransaction,
            privateArguments
        );
    }
    
    public PrivateTransaction getOriginalTransaction() {
        return this.originalTransaction;
    }

    public PrivateTransaction getBlindedTransaction() {
        return this.blindedTransaction;
    }

    public Bytes getPrivateArguments() {
        return this.privateArguments;
    }
}
