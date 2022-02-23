package com.quorum.tessera.q2t.internal;

import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateDataHandler.class);
    
    private final int DATA_LENGTH = 32; // Number of bytes for each data item

    private final byte[] raw;

    private final byte[] addr1;

    private final byte[] addr2;

    // TODO: change items to int
    private final int numItems;

    private final byte[] rawByte;

    // TODO: change itemsRe to int
    private final int numItemsRe;

    private final byte[][] messages;

    public PrivateDataHandler(byte[] raw){
        this.raw = raw;
        Pointer p = new Pointer(DATA_LENGTH);
        this.addr1 = Arrays.copyOfRange(raw, p.getPointer(), p.nextStep());
        this.addr2 = Arrays.copyOfRange(raw, p.getPointer(), p.nextStep());
        this.numItems = ByteBuffer.wrap(Arrays.copyOfRange(raw, p.getPointer(), p.nextStep())).getInt(28);    //getInt only reads 4 bytes
        this.rawByte = Arrays.copyOfRange(raw, p.getPointer(), p.nextStep());
        this.numItemsRe = ByteBuffer.wrap(Arrays.copyOfRange(raw, p.getPointer(), p.nextStep())).getInt(28);   //getInt only reads 4 bytes
        this.messages = new byte[this.numItems][DATA_LENGTH];
        for(int i = 0; i < this.numItems; i++){
            this.messages[i] = Arrays.copyOfRange(raw, p.getPointer(), p.nextStep());
        }
    }

    public byte[] getAddr1() {
        return this.addr1;
    }

    public byte[] getAddr2() {
        return this.addr2;
    }

    public int getNumberOfItems() {
        return this.numItems;
    }

    public byte[][] getMessages() {
        return this.messages;
    }

    private class Pointer{
        private int p;
        private int step;
        private Pointer(int step){
            this.p = 0;
            this.step = step;
        }
        public int getPointer(){
            return this.p;
        }
        public int nextStep(){
            this.p += this.step;
            return this.p;
        }
    }

    @Override
    public String toString(){
        String str = "";
        str += "PrivateData";
        //str += "\n  addr1: " + this.addr1.toString();
        //str += "\n  addr2: " + this.addr2.toString();
        return str;
    }

}
