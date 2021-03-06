package com.meter.sdk.model.clients;

import com.meter.sdk.base.BaseTest;
import com.meter.sdk.core.model.clients.Amount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AmountTest extends BaseTest {

    @Test
    public void testAmount() {
        int token = 1;
        String hex = "0x000000000000000000000000000000000000000000000000a688906bd8b00000";
        Amount amount = Amount.ERC20Amount(token);
        amount.setDecimalAmount(hex);
        logger.info("Decimal amount:" + amount.getAmount().toString());
    }

}
