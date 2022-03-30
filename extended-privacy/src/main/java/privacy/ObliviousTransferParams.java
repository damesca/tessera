package extended.privacy;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.besu.ethereum.rlp.RLP;
import org.hyperledger.besu.ethereum.rlp.RLPInput;

import org.apache.tuweni.bytes.Bytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObliviousTransferParams {
    private static final Logger LOG = LoggerFactory.getLogger(ObliviousTransferParams.class);

    private static final Bytes zeroBytes = Bytes.fromHexString("0000000000000000000000000000000000000000000000000000000000000000");
    //private static final Bytes zeroBytes = Bytes.fromHexString("0000000000000000000000000000000000000000000000090909090909090909");

    private final Bytes senderAddr;
    private final Bytes receiverAddr;
    private final int numberOfItems;
    private final List<Bytes> itemList;
    private final Bytes noInfoArgs;

    protected ObliviousTransferParams(
            final Bytes senderAddr,
            final Bytes receiverAddr,
            final int numberOfItems,
            final List<Bytes> itemList,
            final Bytes noInfoArgs) {
        this.senderAddr = senderAddr;
        this.receiverAddr = receiverAddr;
        this.numberOfItems = numberOfItems;
        this.itemList = itemList;  
        this.noInfoArgs = noInfoArgs;  
    }

    public static ObliviousTransferParams readFrom(final Bytes input){
        
        List<Bytes> inputList = new ArrayList<Bytes>();

        for(int i = 0; i < input.size()/32; i++) {
            inputList.add(input.slice(0+i*32,32));
        }
        
        Bytes senderAddr = inputList.get(0).slice(12);
        Bytes receiverAddr = inputList.get(1).slice(12);
        int numberOfItems = inputList.get(2).getInt(28);
        // TODO: check consistency for numberOfItems

        List<Bytes> itemList = new ArrayList<Bytes>();
        for(int i = 0; i < numberOfItems; i++) {
            itemList.add(inputList.get(5+i));
            inputList.set(5+i, zeroBytes);
        }

        Bytes noInfoArgs = Bytes.concatenate(inputList);

        return new ObliviousTransferParams(
            senderAddr,
            receiverAddr,
            numberOfItems,
            itemList,
            noInfoArgs
        );
    }
    
    public Bytes getSenderAddr() {
        return this.senderAddr;
    }

    public Bytes getReceiverAddr() {
        return this.receiverAddr;
    }

    public int getNumberOfItems() {
        return this.numberOfItems;
    }

    public List<Bytes> getItems() {
        return this.itemList;
    }

    public Bytes getNoInfoArgs() {
        return this.noInfoArgs;
    }
}
