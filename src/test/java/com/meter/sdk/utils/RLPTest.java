package com.meter.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.base.BaseTest;
import com.meter.sdk.core.model.blockchain.RawClause;
import com.meter.sdk.core.model.clients.Amount;
import com.meter.sdk.core.model.clients.RawTransaction;
import com.meter.sdk.utils.rlp.RlpEncoder;
import com.meter.sdk.utils.rlp.RlpDecoder;
import com.meter.sdk.utils.rlp.RlpList;
import com.meter.sdk.utils.rlp.RlpString;
import com.meter.sdk.utils.rlp.RlpType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class RLPTest extends BaseTest {

    @Test
    public void testByteArray() {
        List<RlpType> result = new ArrayList<>();
        result.add(RlpString.create(new byte[] {}));

        RlpList rlpList = new RlpList(result);
        byte[] empty = RlpEncoder.encode(rlpList);

        logger.info("RLP empty array:" + BytesUtils.toHexString(empty, Prefix.ZeroLowerX));

    }

    @Test
    public void testZero() {
        List<RlpType> result = new ArrayList<>();
        RlpList rlpList = new RlpList(result);
        result.add(RlpString.create(BigInteger.ZERO));
        byte[] empty = RlpEncoder.encode(rlpList);

        logger.info("RLP zero array:" + BytesUtils.toHexString(empty, Prefix.ZeroLowerX));
    }

    @Test
    public void testBytesZero() {
        List<RlpType> result = new ArrayList<>();
        byte zeros[] = new byte[32];
        result.add(RlpString.create(zeros));

        RlpList rlpList = new RlpList(result);
        byte[] empty = RlpEncoder.encode(rlpList);
        logger.info("RLP zeros array:" + BytesUtils.toHexString(empty, Prefix.ZeroLowerX));
    }

    @Test
    public void testDecodeRlp() {
        String hexRaw = "0xf83d81c7860881eec535498202d0e1e094000000002beadb038203be21ed5ce7c9b1bff60289056bc75e2d63100000808082520880884773cc184328eb3ec0";
        RawTransaction rawTransaction = RLPUtils.decode(hexRaw);
        byte[] encoded = RLPUtils.encodeRawTransaction(rawTransaction);
        String hexEncoded = BytesUtils.toHexString(encoded, Prefix.ZeroLowerX);
        Assert.assertEquals(hexRaw, hexEncoded);
    }

    public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    @Test
    public void testDecodeRawTxn() {
        String hexRawTxn = "0xa9059cbb00000000000000000000000067e37c1896fe00284d7dcc7fdfc61810c10c004f000000000000000000000000000000000000000000000000016345785d8a0000";

        String to = "0x" + hexRawTxn.substring(10, 74).replaceFirst("^0+(?!$)", "");
        String value = "0x" + hexRawTxn.substring(74).replaceFirst("^0+(?!$)", "");

        Long decimal = Long.parseLong("16345785d8a0000", 16);

        logger.info("To:" + JSON.toJSONString(to) + "  Amount:" + JSON.toJSONString(decimal));

    }
}
