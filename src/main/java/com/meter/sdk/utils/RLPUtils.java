package com.meter.sdk.utils;

import com.meter.sdk.core.model.blockchain.RawClause;
import com.meter.sdk.core.model.clients.RawTransaction;
import com.meter.sdk.core.model.clients.TransactionReserved;
import com.meter.sdk.utils.rlp.*;

import java.util.ArrayList;
import java.util.List;

/**
 * RLP encoding utility
 */
public class RLPUtils {
    private final static int Chain_Tag = 0;
    private final static int Block_Ref = 1;
    private final static int Expiration = 2;
    private final static int Clauses = 3;
    private final static int GasPriceCoef = 4;
    private final static int Gas = 5;
    private final static int DependsOn = 6;
    private final static int Nonce = 7;
    private final static int Reserved = 8;
    private final static int Signature = 9;

    private final static int To = 0;
    private final static int Value = 1;
    private final static int Token = 2;
    private final static int Data = 3;

    public static byte[] encodeRawTransaction(RawTransaction rawTransaction) {
        List<RlpType> values = asRlpValues(rawTransaction);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private static List<RlpType> asRlpValues(RawTransaction rawTransaction) {
        List<RlpType> result = new ArrayList<>();
        if (rawTransaction.getChainTag() == 0) {
            throw new IllegalArgumentException("getChainTag is null");
        }
        result.add(RlpString.create(rawTransaction.getChainTag()));

        if (rawTransaction.getBlockRef() == null) {
            throw new IllegalArgumentException("getBlockRef is null");
        }
        result.add(RlpString.create(rawTransaction.getBlockRef()));

        if (rawTransaction.getExpiration() == null) {
            throw new IllegalArgumentException("getExpiration is null");
        }
        result.add(RlpString.create(rawTransaction.getExpiration()));

        List<RlpType> clauses = buildRlpClausesLIst(rawTransaction);
        RlpList rlpList = new RlpList(clauses);
        result.add(rlpList);

        if (rawTransaction.getGasPriceCoef() == 0) {
            result.add(RlpString.create(RlpString.EMPTY));
        } else {
            result.add(RlpString.create(rawTransaction.getGasPriceCoef()));
        }

        if (rawTransaction.getGas() == null) {
            throw new IllegalArgumentException("getGas is null");
        }
        result.add(RlpString.create(rawTransaction.getGas()));

        if (rawTransaction.getDependsOn() == null) {
            result.add(RlpString.create(RlpString.EMPTY));
        } else {
            result.add(RlpString.create(rawTransaction.getDependsOn()));
        }

        if (rawTransaction.getNonce() == null) {
            throw new IllegalArgumentException("getNonce is null");
        }
        result.add(RlpString.create(rawTransaction.getNonce()));

        if (rawTransaction.getReserved() == null) {
            List<RlpType> reservedRlp = new ArrayList<>();
            RlpList reservedList = new RlpList(reservedRlp);
            result.add(reservedList);
        } else {
            List<RlpType> reservedRlpList = new ArrayList<>();
            for (byte[] reservedValue : rawTransaction.getReserved().getReservedValues()) {
                reservedRlpList.add(RlpString.create(reservedValue));
            }
            RlpList reservedList = new RlpList(reservedRlpList);
            result.add(reservedList);
        }

        if (rawTransaction.getSignature() != null) {
            result.add(RlpString.create(rawTransaction.getSignature()));
        }
        return result;

    }

    private static List<RlpType> buildRlpClausesLIst(RawTransaction rawTransaction) {
        List<RlpType> clauses = new ArrayList<>();

        for (RawClause clause : rawTransaction.getClauses()) {

            List<RlpType> rlpClause = new ArrayList<>();
            if (clause.getTo() == null) {
                rlpClause.add(RlpString.create(RlpString.EMPTY));
            } else {
                rlpClause.add(RlpString.create(clause.getTo()));
            }

            if (clause.getValue() == null) {
                rlpClause.add(RlpString.create(RlpString.EMPTY));
            } else {
                rlpClause.add(RlpString.create(clause.getValue()));
            }

            if (clause.getToken() == null) {
                rlpClause.add(RlpString.create(RlpString.EMPTY));
            } else {
                rlpClause.add(RlpString.create(clause.getToken()));
            }

            if (clause.getData() == null) {
                rlpClause.add(RlpString.create(RlpString.EMPTY));
            } else {
                rlpClause.add(RlpString.create(clause.getData()));
            }
            RlpList clauseRLP = new RlpList(rlpClause);
            clauses.add(clauseRLP);
        }
        return clauses;
    }

    /**
     * Decode hex string
     * 
     * @param hexRawTransaction hex raw transaction
     * @return
     */
    public static RawTransaction decode(String hexRawTransaction) {
        if (!StringUtils.isHex(hexRawTransaction)) {
            return null;
        }
        byte[] rawTxBytes = BytesUtils.toByteArray(hexRawTransaction);
        RlpList list = RlpDecoder.decode(rawTxBytes);
        if (list == null) {
            return null;
        }
        List<RlpType> rlpContent = list.getValues();
        // It should only has one element.
        if (rlpContent.size() != 1) {
            return null;
        }
        RawTransaction rawTransaction = new RawTransaction();
        List listValues = ((RlpList) rlpContent.get(0)).getValues();
        for (int index = 0; index < listValues.size(); index++) {
            fillTransaction(rawTransaction, listValues, index);
        }
        return rawTransaction;
    }

    private static void fillTransaction(RawTransaction rawTransaction, List listValues, int index) {
        RlpString rlpString;
        RlpList clauseList;
        switch (index) {
            case Chain_Tag:
                rlpString = (RlpString) listValues.get(index);
                rawTransaction.setChainTag(rlpString.getBytes()[0]);
                break;
            case Block_Ref:
                rlpString = (RlpString) listValues.get(index);
                rawTransaction.setBlockRef(rlpString.getBytes());
                break;
            case Expiration:
                rlpString = (RlpString) listValues.get(index);
                rawTransaction.setExpiration(rlpString.getBytes());
                break;
            case Clauses:
                clauseList = (RlpList) listValues.get(index);
                fillClauses(rawTransaction, clauseList);
                break;
            case GasPriceCoef:
                rlpString = (RlpString) listValues.get(index);
                if (rlpString.getBytes().length == 0) {
                    rawTransaction.setGasPriceCoef((byte) 0);
                } else {
                    rawTransaction.setGasPriceCoef(rlpString.getBytes()[0]);
                }

                break;
            case Gas:
                rlpString = (RlpString) listValues.get(index);
                rawTransaction.setGas(rlpString.getBytes());
                break;
            case DependsOn:
                rlpString = (RlpString) listValues.get(index);
                if (rlpString.getBytes().length == 0) {
                    rawTransaction.setDependsOn(null);
                } else {
                    rawTransaction.setDependsOn(rlpString.getBytes());
                }

                break;
            case Nonce:
                rlpString = (RlpString) listValues.get(index);
                rawTransaction.setNonce(rlpString.getBytes());
                break;
            case Reserved:
                RlpList rlpList = (RlpList) listValues.get(index);
                fillReserved(rlpList, rawTransaction);
                break;
            case Signature:
                rlpString = (RlpString) listValues.get(index);
                rawTransaction.setSignature(rlpString.getBytes());
                break;
        }
    }

    private static void fillReserved(RlpList rlpList, RawTransaction rawTransaction) {

        List<RlpType> rlpTypeList = rlpList.getValues();
        TransactionReserved transactionReserved = new TransactionReserved();
        for (RlpType rlpType : rlpTypeList) {
            RlpString reservedRlpString = (RlpString) rlpType;
            byte[] reservedBytes = reservedRlpString.getBytes();
            transactionReserved.getReservedValues().add(reservedBytes);
        }
        rawTransaction.setReserved(transactionReserved);
    }

    private static void fillClauses(RawTransaction rawTransaction, RlpList list) {
        List clauses = (List) list.getValues();
        int clausesSize = clauses.size();
        RawClause[] rawClause = new RawClause[clausesSize];
        rawTransaction.setClauses(rawClause);
        for (int clauseIndex = 0; clauseIndex < clausesSize; clauseIndex++) {
            List<RlpType> clauseContent = ((RlpList) clauses.get(clauseIndex)).getValues();
            rawClause[clauseIndex] = new RawClause();
            fillOneClause(rawClause, clauseIndex, clauseContent);
        }
    }

    private static void fillOneClause(RawClause[] rawClause, int clauseIndex, List<RlpType> clauseContent) {
        for (int index = 0; index < clauseContent.size(); index++) {
            RlpString clause = (RlpString) clauseContent.get(index);
            switch (index) {
                case To:
                    rawClause[clauseIndex].setTo(clause.getBytes());
                    break;
                case Value:
                    rawClause[clauseIndex].setValue(clause.getBytes());
                    break;
                case Token:
                    rawClause[clauseIndex].setToken(clause.getBytes());
                    break;
                case Data:
                    rawClause[clauseIndex].setData(clause.getBytes());
                    break;
            }
        }
    }

}
