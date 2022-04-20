package com.meter.sdk.model.clients;

import com.meter.sdk.base.BaseTest;
import com.meter.sdk.core.model.clients.StorageKey;
import com.meter.sdk.utils.BytesUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StorageKeyTest extends BaseTest {

    @Test
    public void testStorageKey() {
        byte[] address = BytesUtils.toByteArray(fromAddress);
        StorageKey key = StorageKey.create(4, address);
        logger.info("key hex:" + key.hexKey());
    }

}
