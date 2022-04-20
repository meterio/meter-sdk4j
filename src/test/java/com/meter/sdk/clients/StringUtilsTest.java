package com.meter.sdk.clients;

import com.meter.sdk.base.BaseTest;
import com.meter.sdk.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StringUtilsTest extends BaseTest {

    @Test
    public void testIsCriticalHex() {
        Assert.assertTrue(StringUtils.isCriticalHex("0x1"));
        Assert.assertTrue(StringUtils.isCriticalHex("0x01"));
        Assert.assertTrue(!StringUtils.isCriticalHex("0x"));
        Assert.assertTrue(!StringUtils.isCriticalHex("0"));
        Assert.assertTrue(!StringUtils.isCriticalHex("3f"));
        Assert.assertTrue(!StringUtils.isCriticalHex(null));
        Assert.assertTrue(!StringUtils.isCriticalHex("0x3frff"));
        Assert.assertTrue(!StringUtils.isCriticalHex("abcdefg"));
        Assert.assertTrue(!StringUtils.isCriticalHex("4324432"));
    }
}
