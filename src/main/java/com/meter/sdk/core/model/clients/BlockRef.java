package com.meter.sdk.core.model.clients;

import com.meter.sdk.utils.BytesUtils;
import com.meter.sdk.utils.Prefix;
import com.meter.sdk.utils.StringUtils;

/**
 * Created by albertma on 2018/6/23.
 */
public class BlockRef {

    private byte[] blockRef;

    /**
     * Constructor of BlockRef;
     * 
     * @param blockIdBytes
     */
    private BlockRef(byte[] blockIdBytes) {
        this.blockRef = new byte[8];
        System.arraycopy(blockIdBytes, 0, blockRef, 0, 8);
    }

    /**
     * Create block reference from block hex string
     * 
     * @param hexBlockId hex string start with "0x"
     * @return {@link BlockRef} block reference used to send transaction.
     */
    public static BlockRef create(String hexBlockId) {
        if (!StringUtils.isHex(hexBlockId)) {
            throw new IllegalArgumentException("hex block id is invalid");
        }
        byte[] blockIdBytes = BytesUtils.toByteArray(hexBlockId);
        return new BlockRef(blockIdBytes);
    }

    /**
     * To hex string
     * 
     * @return hex string with prefix "0x".
     */
    public String toString() {
        return BytesUtils.toHexString(this.blockRef, Prefix.ZeroLowerX);
    }

    /**
     * Convert to byte array.
     * 
     * @return byte array.
     */
    public byte[] toByteArray() {
        return this.blockRef;
    }
}
