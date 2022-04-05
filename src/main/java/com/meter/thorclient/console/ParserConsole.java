package com.meter.thorclient.console;

import com.meter.thorclient.core.model.blockchain.RawClause;
import com.meter.thorclient.core.model.clients.Amount;
import com.meter.thorclient.core.model.clients.ERC20Contract;
import com.meter.thorclient.core.model.clients.RawTransaction;
import com.meter.thorclient.core.model.clients.base.AbiDefinition;
import com.meter.thorclient.utils.BytesUtils;
import com.meter.thorclient.utils.Prefix;
import com.meter.thorclient.utils.RLPUtils;

public class ParserConsole {
    public static void parse(String[] args) throws Exception {
        if(args.length != 3){
            System.out.println( "parse [MTR|erc20] [raw transaction hex string]" );
            return;
        }
        if(args[1].equalsIgnoreCase( "MTR" )){
            parseMTR(args[2]);
        }else if(args[1].equalsIgnoreCase( "erc20" )||args[1].equalsIgnoreCase( "MTRG" )){
            parseERC20(args[2]);
        }else{
            System.out.println( "parse [MTR|erc20] [raw transaction hex string]" );
            throw new Exception( "un-support tx type" );
        }
    }

    public static void parseMTR(String hexRawTxn) {
        RawTransaction rawTransaction =  RLPUtils.decode(hexRawTxn );
        RawClause[] rawClauses = rawTransaction.getClauses();
        int index = 1;
        System.out.println( "----------------------------------------------------------");
        System.out.println( "ChainTag:" + rawTransaction.getChainTag());
        System.out.println( "BlockRef:" + BytesUtils.toHexString(rawTransaction.getBlockRef(), Prefix.ZeroLowerX) );
        System.out.println( "Expiration:" + BytesUtils.bytesToBigInt(rawTransaction.getExpiration()).toString() );
        System.out.println( "Gas:" + BytesUtils.bytesToBigInt(rawTransaction.getGas()).toString() );
        for(RawClause rawClause : rawClauses){
            byte[] addressBytes = rawClause.getTo();
            byte[] valueBytes = rawClause.getValue();
            Amount amount = Amount.MTR();
            if (valueBytes == null || valueBytes.length == 0){
                amount.setHexAmount( "0x00" );
            }else{
                amount.setHexAmount( BytesUtils.toHexString( valueBytes, Prefix.ZeroLowerX ) );
            }

            System.out.println( "No." + index);
            System.out.println( "Address:" + BytesUtils.toHexString( addressBytes, Prefix.ZeroLowerX ) );
            System.out.println( "Value:" + amount.getAmount().toPlainString());
            System.out.println( "-----" );
            index++;
        }
        System.out.println( "----------------------------------------------------------" );
    }

    public static void parseERC20(String hexRawTxn) throws Exception {
        RawTransaction rawTransaction =  RLPUtils.decode(hexRawTxn );
        RawClause[] rawClauses = rawTransaction.getClauses();
        int index = 1;
        System.out.println( "----------------------------------------------------------");
        System.out.println( "ChainTag:" + rawTransaction.getChainTag());
        System.out.println( "BlockRef:" + BytesUtils.toHexString(rawTransaction.getBlockRef(), Prefix.ZeroLowerX) );
        System.out.println( "Expiration:" + BytesUtils.bytesToBigInt(rawTransaction.getExpiration()).toString() );
        System.out.println( "Gas:" + BytesUtils.bytesToBigInt(rawTransaction.getGas()).toString() );
        for(RawClause rawClause : rawClauses){
            System.out.println( "No." + index);
            byte[] addressBytes = rawClause.getTo();
            byte[] dataBytes = rawClause.getData();
            if(dataBytes.length != 68){
                throw new Exception( "The data length is not 68 bytes" );
            }
            byte[] methodId = new byte[4];
            byte[] address = new byte[20];
            byte[] value = new byte[32];
            System.arraycopy( dataBytes, 0, methodId, 0, 4 );
            System.arraycopy( dataBytes, 16, address, 0, 20);
            System.arraycopy( dataBytes, 36, value, 0, 32);
            String methodIdHex = BytesUtils.toHexString( methodId, Prefix.ZeroLowerX );
            ERC20Contract contract = new ERC20Contract();
            AbiDefinition abiDefinition = contract.findAbiDefinition( "transfer" );
            String transferMethodId = "0x" + abiDefinition.getHexMethodCodeNoPefix();
            if(!methodIdHex.equalsIgnoreCase( transferMethodId)){
                throw new Exception( "the method id is not transfer" );
            }
            Amount erc2Amount = Amount.MTRG();
            erc2Amount.setHexAmount( BytesUtils.toHexString( value, Prefix.ZeroLowerX ) );
            System.out.println( "ERC20 Contract:" + BytesUtils.toHexString( addressBytes, Prefix.ZeroLowerX ) );
            System.out.println( "To Address:" + BytesUtils.toHexString( address, Prefix.ZeroLowerX ) );
            System.out.println( "To Value:" + erc2Amount.getAmount().toPlainString());
            System.out.println( "-----" );
            index++;
        }
        System.out.println( "----------------------------------------------------------" );
    }

}
