package com.meter.sdk.core.model.clients;

import com.meter.sdk.utils.BytesUtils;
import com.meter.sdk.utils.CryptoUtils;
import com.meter.sdk.utils.Prefix;

public class StorageKey {
    private byte[] data;

    public static StorageKey create(int index, byte[] value) {

        if (index < 0) {
            throw new IllegalArgumentException("index is invalid.");
        }
        if (value == null || value.length > 32) {
            throw new IllegalArgumentException("value is invalid.");
        }
        byte[] indexBytes = new byte[32];
        byte[] valueBytes = value;

        byte[] originIndexBytes = BytesUtils.longToBytes(index);
        System.arraycopy(originIndexBytes, 0, indexBytes, indexBytes.length - originIndexBytes.length,
                originIndexBytes.length);

        if (value.length < 32) {
            valueBytes = new byte[32];
            System.arraycopy(value, 0, valueBytes, valueBytes.length - value.length, value.length);
        }
        return new StorageKey(indexBytes, valueBytes);
    }

    private StorageKey(byte[] index, byte[] value) {
        this.data = new byte[64];
        System.arraycopy(index, 0, data, 0, index.length);
        System.arraycopy(value, 0, data, index.length, value.length);

    }

    public String hexKey() {
        byte[] key = CryptoUtils.keccak256(data);
        return BytesUtils.toHexString(key, Prefix.ZeroLowerX);
    }
}
