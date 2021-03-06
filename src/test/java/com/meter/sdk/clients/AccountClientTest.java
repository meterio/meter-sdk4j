package com.meter.sdk.clients;

import java.io.IOException;

import com.meter.sdk.core.model.clients.Revision;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.base.BaseTest;
import com.meter.sdk.core.model.blockchain.Account;
import com.meter.sdk.core.model.blockchain.AccountCode;
import com.meter.sdk.core.model.blockchain.StorageData;
import com.meter.sdk.core.model.clients.Address;
import com.meter.sdk.core.model.clients.StorageKey;
import com.meter.sdk.utils.BytesUtils;

@RunWith(JUnit4.class)
public class AccountClientTest extends BaseTest {

    @Test
    public void testGetAccountInfo() throws IOException {
        Address address = Address.fromHexString(fromAddress);
        Account account = AccountClient.getAccountInfo(address, Revision.create(22673346));
        logger.info("account info:" + JSON.toJSONString(account));
        logger.info("MTRG:" + account.MTRGBalance().getAmount() + " MTR:" + account.MTRBalance().getAmount());
        Assert.assertNotNull(account);
    }

    @Test
    public void testGetStorageAt() throws IOException {
        byte[] address = BytesUtils.toByteArray(fromAddress);
        StorageKey key = StorageKey.create(4, address);
        StorageData data = AccountClient.getStorageAt(Address.MTRG_Address, key, null);
        logger.info("Storage At:" + JSON.toJSONString(data));
        Assert.assertNotNull(data);
    }

    @Test
    public void testGetCodeTest() throws IOException {
        Address tokenAddr = Address.MTRG_Address;
        AccountCode code = AccountClient.getAccountCode(tokenAddr, null);
        logger.info("code:" + JSON.toJSONString(code));
        Assert.assertNotNull(code);
    }

}
