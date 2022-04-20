package com.meter.thorclient.core.model.clients;

import com.meter.thorclient.core.model.exception.ClientArgumentException;
import com.meter.thorclient.utils.BlockchainUtils;
import com.meter.thorclient.utils.BytesUtils;
import com.meter.thorclient.utils.Prefix;
import com.meter.thorclient.utils.StringUtils;

/**
 * Address object is wrapped address string or byte array.
 */
public class Address {
    public static Address NULL_ADDRESS = new NULLAddress();
    public static Address MTRG_Address = Address.fromHexString( "0x228ebbee999c6a7ad74a6130e81b12f9fe237ba3" );
    public static Address MTR_Address = Address.fromHexString("0x687a6294d0d6d63e751a059bf1ca68e4ae7b13e2");

    private  static final int ADDRESS_SIZE = 20;
    private String sanitizeHexAddress;

    /**
     * Create from byte array.
     * @param addressBytes byte array.
     * @return Address object.
     */
    public static Address fromBytes(byte[] addressBytes){
        if(addressBytes != null && addressBytes.length == ADDRESS_SIZE){
            return new Address( addressBytes );
        }else{
            throw ClientArgumentException.exception( "Address.fromBytes Argument Exception" );
        }
    }

    /**
     * Create from hex address string.
     * @param hexAddress hex string with "0x", "VX" or without prefix.
     * @return Address object.
     */
    public static Address fromHexString(String hexAddress){
        if(StringUtils.isBlank( hexAddress )){
            throw ClientArgumentException.exception( "Address.fromHexString hexAddress is blank string" );
        }
        if(!BlockchainUtils.isAddress(hexAddress)){
            throw ClientArgumentException.exception( "Address.fromHexString hexAddress is not hex format " );
        }
        final String sanitizeHexStr = StringUtils.sanitizeHex( hexAddress );
        return new Address( sanitizeHexStr );

    }

    private Address(byte[] addressBytes){
        this.sanitizeHexAddress = BytesUtils.toHexString( addressBytes, null);
    }

    private Address(String sanitizeHexAddress){
        this.sanitizeHexAddress = sanitizeHexAddress;
    }

    private Address(){}
    /**
     * Convert Address to byte array.
     * @return byte[] value.
     */
    public byte[] toByteArray(){
        return BytesUtils.toByteArray( this.sanitizeHexAddress );
    }

    /**
     * Convert Address to hex string with prefix.
     * @param prefix {@link Prefix} optional can be null.
     * @return hex string.
     */
    public String toHexString(Prefix prefix){
        if(prefix != null){
            return prefix.getPrefixString() +  this.sanitizeHexAddress;
        }else{
            return this.sanitizeHexAddress;
        }
    }

    private static class NULLAddress extends Address{
        public byte[] toByteArray(){
            return new byte[]{};
        }
    }

}
