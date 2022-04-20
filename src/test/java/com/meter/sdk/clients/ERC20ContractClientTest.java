package com.meter.sdk.clients;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.base.BaseTest;
import com.meter.sdk.core.model.blockchain.Receipt;
import com.meter.sdk.core.model.blockchain.TransferResult;
import com.meter.sdk.core.model.clients.Address;
import com.meter.sdk.core.model.clients.Amount;
import com.meter.sdk.core.model.clients.ERC20Token;
import com.meter.sdk.utils.crypto.ECKeyPair;

@RunWith(JUnit4.class)
public class ERC20ContractClientTest extends BaseTest {

	@Test
	public void testERC20GetBalance() throws IOException {
		Address address = Address.fromHexString(fromAddress);
		int token = 1;
		Amount balance = ERC20ContractClient.getERC20Balance(address, token, null);
		if (balance != null) {
			if (token == 1) {
				logger.info("Get MTRG:" + balance.getAmount());
			} else {
				logger.info("Get MTR:" + balance.getAmount());
			}

		}

		Assert.assertNotNull(balance);
	}

	@Test
	public void testSendERC20Token() {
		String toAmount = "0.01";
		String toAddress = "0x67E37c1896Fe00284D7dcC7fDfc61810C10C004F";
		Address address = Address.fromHexString(toAddress);
		int token = 1;
		Amount balance = ERC20ContractClient.getERC20Balance(address, token, null);
		if (balance != null) {
			if (token == 1) {
				logger.info("Get MTRG Before:" + balance.getAmount());
			} else {
				logger.info("Get MTR Before:" + balance.getAmount());
			}
		}

		Amount amount = Amount.ERC20Amount(token);
		amount.setDecimalAmount(toAmount);
		TransferResult result = ERC20ContractClient.transferERC20Token(
				new Address[] { Address.fromHexString(toAddress) }, new Amount[] { amount }, 1000000,
				(byte) 0x0, 720,
				ECKeyPair.create(privateKey), token);

		logger.info("sendERC20Token: " + JSON.toJSONString(result));

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}

		Receipt receipt = TransactionClient.getTransactionReceipt(result.getId(), null);
		logger.info("Receipt:" + JSON.toJSONString(receipt));

		Amount balance2 = ERC20ContractClient.getERC20Balance(address, token, null);
		if (balance2 != null) {
			if (token == 1) {
				logger.info("Get MTRG After:" + balance.getAmount());
			} else {
				logger.info("Get MTR After:" + balance.getAmount());
			}
		}
		Assert.assertEquals(0,
				amount.getAmount().subtract(balance2.getAmount().subtract(balance.getAmount()))
						.longValue());

	}

}
