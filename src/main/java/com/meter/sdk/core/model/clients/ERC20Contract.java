package com.meter.sdk.core.model.clients;

import com.meter.sdk.core.model.clients.base.AbiDefinition;
import com.meter.sdk.core.model.clients.base.AbstractContract;
import com.meter.sdk.utils.BytesUtils;

public class ERC20Contract extends AbstractContract {
    private static final String ERC20ABIString = "[\n" +
            " {\n" +
            "  \"constant\": false,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"name\": \"spender\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"name\": \"value\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"approve\",\n" +
            "  \"outputs\": [\n" +
            "   {\n" +
            "    \"name\": \"\",\n" +
            "    \"type\": \"bool\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"payable\": false,\n" +
            "  \"stateMutability\": \"nonpayable\",\n" +
            "  \"type\": \"function\"\n" +
            " },\n" +
            " {\n" +
            "  \"constant\": true,\n" +
            "  \"inputs\": [],\n" +
            "  \"name\": \"totalSupply\",\n" +
            "  \"outputs\": [\n" +
            "   {\n" +
            "    \"name\": \"\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"payable\": false,\n" +
            "  \"stateMutability\": \"view\",\n" +
            "  \"type\": \"function\"\n" +
            " },\n" +
            " {\n" +
            "  \"constant\": false,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"name\": \"from\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"name\": \"to\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"name\": \"value\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"transferFrom\",\n" +
            "  \"outputs\": [\n" +
            "   {\n" +
            "    \"name\": \"\",\n" +
            "    \"type\": \"bool\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"payable\": false,\n" +
            "  \"stateMutability\": \"nonpayable\",\n" +
            "  \"type\": \"function\"\n" +
            " },\n" +
            " {\n" +
            "  \"constant\": true,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"name\": \"who\",\n" +
            "    \"type\": \"address\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"balanceOf\",\n" +
            "  \"outputs\": [\n" +
            "   {\n" +
            "    \"name\": \"\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"payable\": false,\n" +
            "  \"stateMutability\": \"view\",\n" +
            "  \"type\": \"function\"\n" +
            " },\n" +
            " {\n" +
            "  \"constant\": false,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"name\": \"to\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"name\": \"value\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"transfer\",\n" +
            "  \"outputs\": [\n" +
            "   {\n" +
            "    \"name\": \"\",\n" +
            "    \"type\": \"bool\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"payable\": false,\n" +
            "  \"stateMutability\": \"nonpayable\",\n" +
            "  \"type\": \"function\"\n" +
            " },\n" +
            " {\n" +
            "  \"constant\": true,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"name\": \"owner\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"name\": \"spender\",\n" +
            "    \"type\": \"address\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"allowance\",\n" +
            "  \"outputs\": [\n" +
            "   {\n" +
            "    \"name\": \"\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"payable\": false,\n" +
            "  \"stateMutability\": \"view\",\n" +
            "  \"type\": \"function\"\n" +
            " },\n" +
            " {\n" +
            "  \"anonymous\": false,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"indexed\": true,\n" +
            "    \"name\": \"owner\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"indexed\": true,\n" +
            "    \"name\": \"spender\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"indexed\": false,\n" +
            "    \"name\": \"value\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"Approval\",\n" +
            "  \"type\": \"event\"\n" +
            " },\n" +
            " {\n" +
            "  \"anonymous\": false,\n" +
            "  \"inputs\": [\n" +
            "   {\n" +
            "    \"indexed\": true,\n" +
            "    \"name\": \"from\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"indexed\": true,\n" +
            "    \"name\": \"to\",\n" +
            "    \"type\": \"address\"\n" +
            "   },\n" +
            "   {\n" +
            "    \"indexed\": false,\n" +
            "    \"name\": \"value\",\n" +
            "    \"type\": \"uint256\"\n" +
            "   }\n" +
            "  ],\n" +
            "  \"name\": \"Transfer\",\n" +
            "  \"type\": \"event\"\n" +
            " }\n" +
            "]";

    public ERC20Contract() {
        super(ERC20ABIString);
    }

    /**
     * Build transfer to clause.
     * 
     * @param token     required token to transfer.
     * @param toAddress transfer to address.
     * @param amount    amount
     * @return
     */
    public static ToClause buildERC20TranferToClause(Address toAddress, Amount amount, int token) {

        if (toAddress == null) {
            throw new IllegalArgumentException("toAddress is null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount is null");
        }

        AbiDefinition abiDefinition = defaultERC20Contract.findAbiDefinition("transfer");
        if (abiDefinition == null) {
            throw new RuntimeException("can not find transfer abi method");
        }
        String data = buildData(abiDefinition, toAddress.toHexString(null), amount.toBigInteger());

        ToData toData = new ToData();
        toData.setData(data);

        Token tokenAddress = token == 0 ? Token.MTR_TOKEN : Token.MTRG_TOKEN;

        Address sysContractAddr = token == 0 ? Address.MTR_Address : Address.MTRG_Address;
        return new ToClause(sysContractAddr, Amount.ZERO, toData, tokenAddress);
    }

    public static final ERC20Contract defaultERC20Contract = new ERC20Contract();

}
