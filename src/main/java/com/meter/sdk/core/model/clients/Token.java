package com.meter.sdk.core.model.clients;

import com.meter.sdk.core.model.exception.ClientArgumentException;
import com.meter.sdk.utils.BlockchainUtils;
import com.meter.sdk.utils.BytesUtils;
import com.meter.sdk.utils.Prefix;
import com.meter.sdk.utils.StringUtils;

/**
 * Token object is wrapped address string or byte array.
 */
public class Token {
    public static Token NULL_TOKEN = new NULLToken();
    public static Token MTRG_TOKEN = Token.fromHexString("x1");
    public static Token MTR_TOKEN = Token.fromHexString("x0");

    private static final int TOKEN_SIZE = 20;
    private String sanitizeHexToken;

    /**
     * Create from byte array.
     * 
     * @param tokenBytes byte array.
     * @return Token object.
     */
    public static Token fromBytes(byte[] tokenBytes) {
        if (tokenBytes != null && tokenBytes.length == TOKEN_SIZE) {
            return new Token(tokenBytes);
        } else {
            throw ClientArgumentException.exception("Token.fromBytes Argument Exception");
        }
    }

    /**
     * Create from hex token string.
     * 
     * @param hexToken hex string with "0x", "VX" or without prefix.
     * @return Token object.
     */
    public static Token fromHexString(String hexToken) {
        if (StringUtils.isBlank(hexToken)) {
            throw ClientArgumentException.exception("Token.fromHexString hexAddress is blank string");
        }

        final String sanitizeHexStr = StringUtils.sanitizeHex(hexToken);
        return new Token(sanitizeHexStr);

    }

    private Token(byte[] tokenBytes) {
        this.sanitizeHexToken = BytesUtils.toHexString(tokenBytes, null);
    }

    private Token(String sanitizeHexToken) {
        this.sanitizeHexToken = sanitizeHexToken;
    }

    private Token() {
    }

    /**
     * Convert Token to byte array.
     * 
     * @return byte[] value.
     */
    public byte[] toByteArray() {
        return BytesUtils.toByteArray(this.sanitizeHexToken);
    }

    /**
     * Convert Token to hex string with prefix.
     * 
     * @param prefix {@link Prefix} optional can be null.
     * @return hex string.
     */
    public String toHexString(Prefix prefix) {
        if (prefix != null) {
            return prefix.getPrefixString() + this.sanitizeHexToken;
        } else {
            return this.sanitizeHexToken;
        }
    }

    private static class NULLToken extends Token {
        public byte[] toByteArray() {
            return new byte[] {};
        }
    }

}
