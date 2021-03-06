package com.meter.sdk.clients;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.base.BaseTest;
import com.meter.sdk.core.model.blockchain.Clause;
import com.meter.sdk.core.model.blockchain.RawClause;
import com.meter.sdk.core.model.blockchain.Receipt;
import com.meter.sdk.core.model.blockchain.Transaction;
import com.meter.sdk.core.model.blockchain.TransferResult;
import com.meter.sdk.core.model.clients.*;
import com.meter.sdk.core.model.clients.base.AbstractToken;
import com.meter.sdk.core.model.exception.ClientIOException;
import com.meter.sdk.utils.*;
import com.meter.sdk.utils.crypto.ECKeyPair;
import com.meter.sdk.utils.crypto.Key;
import org.apache.commons.codec.digest.Crypt;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sun.awt.image.ByteArrayImageSource;

import java.lang.reflect.Array;

@RunWith(JUnit4.class)
public class TransactionClientTest extends BaseTest {

	static String hexId = "0x7c12eae79f5e058317bcf07c6c08e4ba7db1e7b75f075431cb70e191015a7ea6";
	static String addUserTxId = "0x652b5c0f68d03fed86625969ad38e0634993f8da950126518b0c02e6e630d3de";
	static String removeUserTxId = "0x3bec812d64615584414595e050bb52be9c0807cb1c05dc2ea9286a1e7c6a4da0";
	static String setUserPlanTxId = "0x9dbdd7dc102eafe882f9e084ca01671ae8eebe59751ffcfbd1abfeb5cb687846";
	static String addSponsorTxId = "0x010fed13ca1d699674529a0c8621fe1ac61dfdf2f7a6d6fce77fbf7cbb77e092";
	static String selectSponsorTxId = "0x9cc90d37cf088b63b8180ab7978b673822a36000c39b5ce38da525e2a17ea5f0";
	static String unsponsorTxId = "0xe86d6b5546e12741ce894ba25d5c3ed0a16e700ed93e18c6050451592b3f2b8c";
	static String contractHexString = "0x60806040526040805190810160405280600381526020017f312e3000000000000000000000000000000000000000000000000000000000008152506000908051906020019061004f9291906100a3565b5034801561005c57600080fd5b5032600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550610148565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100e457805160ff1916838001178555610112565b82800160010185558215610112579182015b828111156101115782518255916020019190600101906100f6565b5b50905061011f9190610123565b5090565b61014591905b80821115610141576000816000905550600101610129565b5090565b90565b6109df806101576000396000f30060806040526004361061008e576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306661abd1461009357806329ba7bb2146100be57806354fd4d50146101155780638eaa6ac0146101a5578063af640d0f146101f2578063cd10c04b14610282578063f321b305146102d9578063f71f7a251461037a575b600080fd5b34801561009f57600080fd5b506100a86103d1565b6040518082815260200191505060405180910390f35b3480156100ca57600080fd5b506100d36103d7565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561012157600080fd5b5061012a6103fd565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561016a57808201518184015260208101905061014f565b50505050905090810190601f1680156101975780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101b157600080fd5b506101d4600480360381019080803560001916906020019092919050505061049b565b60405180826000191660001916815260200191505060405180910390f35b3480156101fe57600080fd5b506102076104c0565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561024757808201518184015260208101905061022c565b50505050905090810190601f1680156102745780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561028e57600080fd5b5061029761055e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102e557600080fd5b50610360600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610584565b604051808215151515815260200191505060405180910390f35b34801561038657600080fd5b506103b760048036038101908080356000191690602001909291908035600019169060200190929190505050610650565b604051808215151515815260200191505060405180910390f35b60025481565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104935780601f1061046857610100808354040283529160200191610493565b820191906000526020600020905b81548152906001019060200180831161047657829003601f168201915b505050505081565b6000600560008360001916600019168152602001908152602001600020549050919050565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105565780601f1061052b57610100808354040283529160200191610556565b820191906000526020600020905b81548152906001019060200180831161053957829003601f168201915b505050505081565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161480156105e3575060006001805460018160011615610100020316600290049050145b15156105ee57600080fd5b82600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550816001908051906020019061064592919061090e565b506001905092915050565b600080600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16148061080d5750600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614801561080c575061070a6108cc565b60001916600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16639d7cf156336040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b1580156107cb57600080fd5b505af11580156107df573d6000803e3d6000fd5b505050506040513d60208110156107f557600080fd5b810190808051906020019092919050505060001916145b5b151561081857600080fd5b600560008560001916600019168152602001908152602001600020549050600060010283600019161415610874576000600102816000191614151561086f5761086d60016002546108d790919063ffffffff16565b505b61089d565b60006001028160001916141561089c5761089a60016002546108f090919063ffffffff16565b505b5b826005600086600019166000191681526020019081526020016000208160001916905550600191505092915050565b600060018002905090565b60008282111515156108e557fe5b818303905092915050565b600080828401905083811015151561090457fe5b8091505092915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061094f57805160ff191683800117855561097d565b8280016001018555821561097d579182015b8281111561097c578251825591602001919060010190610961565b5b50905061098a919061098e565b5090565b6109b091905b808211156109ac576000816000905550600101610994565b5090565b905600a165627a7a723058207df82c35dde30f7e99f38fb1421936e6ca9559b04287a622971b2e19deec08990029";

	@Test
	public void testGetTransaction() throws ClientIOException {

		Transaction transaction = TransactionClient.getTransaction(hexId, false, null);
		logger.info("Transaction WithOut Raw :" + JSON.toJSONString(transaction));
		// Assert.assertNotNull(transaction);
		// Assert.assertNotNull(transaction.getMeta());
	}

	@Test
	public void testGetTransactionRaw() throws ClientIOException {
		Transaction transaction = TransactionClient.getTransaction(hexId, true, null);
		logger.info("Transaction With Raw:" + JSON.toJSONString(transaction));
		// Assert.assertNotNull(transaction);
		// Assert.assertNotNull(transaction.getRaw());
		// Assert.assertNotNull(transaction.getMeta());
	}

	@Test
	public void testDeployContract() throws ClientIOException {
		TransferResult result = TransactionClient.deployContract(contractHexString, 9000000, (byte) 0, 720,
				ECKeyPair.create(privateKey));
		logger.info("Deploy contract result:" + JSON.toJSONString(result));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		Assert.assertNotNull(result);
		hexId = result.getId();
	}

	@Test
	public void testGetTransactionReceipt() throws ClientIOException {
		Receipt receipt = TransactionClient.getTransactionReceipt(hexId, null);
		logger.info("Receipt:" + JSON.toJSONString(receipt));
		// Assert.assertNotNull(receipt);
		// Assert.assertNotNull(receipt.getMeta());
	}

	@Test
	public void testSendERC20Transaction() throws ClientIOException {
		String toAmount = "0.01";
		int gas = 1000000;
		int expiration = 720;
		byte gasCoef = (byte) 0x0;
		String toAddress = "0x67E37c1896Fe00284D7dcC7fDfc61810C10C004F";
		Address address = Address.fromHexString(toAddress);
		int token = 1;
		System.out.println("privatekey:");
		System.out.println(privateKey);
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
				new Address[] { Address.fromHexString(toAddress) }, new Amount[] { amount }, gas,
				gasCoef, expiration,
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
				logger.info("Get MTRG After:" + balance2.getAmount());
			} else {
				logger.info("Get MTR After:" + balance2.getAmount());
			}

		}
		Assert.assertEquals(0, amount.getAmount().subtract(balance2.getAmount().subtract(balance.getAmount()))
				.longValue());

	}

	@Test
	public void testSendRemarkTx() throws ClientIOException {
		byte chainTag = BlockchainClient.getChainTag();
		byte[] blockRef = BlockchainClient.getBlockRef(Revision.BEST).toByteArray();

		ToData toData = new ToData();
		final int size = 47 * 1000;
		byte[] k64 = new byte[size];

		for (int i = 0; i < size; i++) {
			k64[i] = (byte) 0xff;
		}
		int token = 0;
		toData.setData(BytesUtils.toHexString(k64, Prefix.ZeroLowerX));
		ToClause clause = TransactionClient.buildTransferToClause(
				Address.fromHexString("0x391ba4c2d5212871130f8e05bf9459064d6ccf5b"), Amount.ZERO,
				toData, token);
		RawTransaction rawTransaction = RawTransactionFactory.getInstance().createRawTransaction(chainTag,
				blockRef,
				720, 4000000, (byte) 0x0, CryptoUtils.generateTxNonce(), clause);
		logger.info("Send Raw:" + BytesUtils.toHexString(rawTransaction.encode(), Prefix.ZeroLowerX));
		logger.info("SignHash raw:" + BytesUtils.toHexString(CryptoUtils.blake2b(rawTransaction.encode()),
				Prefix.ZeroLowerX));
		TransferResult result = TransactionClient.signThenTransfer(rawTransaction,
				ECKeyPair.create(privateKey));
		logger.info("Send result:" + JSON.toJSONString(result));
		Assert.assertNotNull(result);
		String hexAddress = ECKeyPair.create(privateKey).getHexAddress();
		String txIdHex = BlockchainUtils.generateTransactionId(rawTransaction,
				Address.fromHexString(hexAddress));
		logger.info("Calculate transaction txid:" + txIdHex);
		Assert.assertEquals(txIdHex, result.getId());
		hexId = result.getId();
	}

	@Test
	public void testSendNativeTransaction() throws ClientIOException {
		byte chainTag = BlockchainClient.getChainTag();
		byte[] blockRef = BlockchainClient.getBlockRef(Revision.BEST).toByteArray();
		int token = 1;
		Amount amount = Amount.createFromToken(AbstractToken.getToken(token));
		amount.setDecimalAmount("0.01");

		ToClause clause = TransactionClient.buildTransferToClause(
				Address.fromHexString("0x67E37c1896Fe00284D7dcC7fDfc61810C10C004F"), amount,
				ToData.ZERO, token);
		RawTransaction rawTransaction = RawTransactionFactory.getInstance().createRawTransaction(chainTag,
				blockRef,
				720, 30000, (byte) 0x0, CryptoUtils.generateTxNonce(), clause);
		logger.info("Send Raw:" + BytesUtils.toHexString(rawTransaction.encode(), Prefix.ZeroLowerX));
		logger.info("SignHash raw:" + BytesUtils.toHexString(CryptoUtils.blake2b(rawTransaction.encode()),
				Prefix.ZeroLowerX));

		TransferResult result = TransactionClient.signThenTransfer(rawTransaction,
				ECKeyPair.create(privateKey));
		logger.info("Send result:" + JSON.toJSONString(result));
		Assert.assertNotNull(result);
		String hexAddress = ECKeyPair.create(privateKey).getHexAddress();
		String txIdHex = BlockchainUtils.generateTransactionId(rawTransaction,
				Address.fromHexString(hexAddress));
		logger.info("Calculate transaction txid:" + txIdHex);
		Assert.assertEquals(txIdHex, result.getId());
		hexId = result.getId();
	}

	private static RawTransaction generatingNativeRawTxn() {
		byte chainTag = BlockchainClient.getChainTag();
		byte[] blockRef = BlockchainClient.getBlockRef(Revision.BEST).toByteArray();
		int token = 0;
		Amount amount = Amount.createFromToken(AbstractToken.getToken(token));

		amount.setDecimalAmount("100");
		ToClause clause = TransactionClient.buildTransferToClause(
				Address.fromHexString("0x000000002beadb038203be21ed5ce7c9b1bff602"), amount,
				ToData.ZERO, token);
		return RawTransactionFactory.getInstance().createRawTransaction(chainTag, blockRef, 720, 31000,
				(byte) 0x0,
				CryptoUtils.generateTxNonce(), clause);
	}

	private static RawTransaction generatingERC20RawTxn() {
		byte chainTag = BlockchainClient.getChainTag();
		byte[] blockRef = BlockClient.getBlock(null).blockRef().toByteArray();
		Amount amount = Amount.createFromToken(ERC20Token.MTRG);
		int token = 1;
		amount.setDecimalAmount("10000");

		ToClause clause = ERC20Contract.buildERC20TranferToClause(
				Address.fromHexString("0x000000002beadb038203be21ed5ce7c9b1bff602"), amount, token);
		RawTransaction rawTransaction = RawTransactionFactory.getInstance().createRawTransaction(chainTag,
				blockRef,
				720, 80000, (byte) 0x0, CryptoUtils.generateTxNonce(), clause);
		return rawTransaction;
	}

	private String rlpEncodedRawTxHex(RawTransaction rawTransaction) throws ClientIOException {
		byte[] rawTxBytes = RLPUtils.encodeRawTransaction(rawTransaction);
		return BytesUtils.toHexString(rawTxBytes, Prefix.ZeroLowerX);
	}

	@Test
	public void testRecoverAddressAndCalcTxId() throws ClientIOException {

		RawTransaction rawTransaction = generatingNativeRawTxn();
		RawTransaction signedRawTxn = TransactionClient.sign(rawTransaction, ECKeyPair.create(privateKey));
		String rawTxHex = rlpEncodedRawTxHex(signedRawTxn);
		logger.info("Tx raw hex:" + rawTxHex);

		Key publicKey = BlockchainUtils.recoverPublicKey(rawTxHex);
		Assert.assertNotNull(publicKey);
		String hexAddress = publicKey.getAddress();
		RawTransaction newRawTransaction = RLPUtils.decode(rawTxHex);
		newRawTransaction.setSignature(null);
		String txIdHex = BlockchainUtils.generateTransactionId(newRawTransaction,
				Address.fromHexString(hexAddress));
		TransferResult transferResult = TransactionClient.transfer(rawTxHex);
		logger.info("Calculate transaction TxId:" + txIdHex);
		logger.info("Send result:" + JSON.toJSONString(transferResult));
		Assert.assertNotNull(transferResult);
		Assert.assertEquals(txIdHex, transferResult.getId());

	}

	@Test
	public void testRecoverAddressAndCalcTxId2() throws ClientIOException {

		RawTransaction rawTransaction = generatingERC20RawTxn();
		RawTransaction signedRawTxn = TransactionClient.sign(rawTransaction, ECKeyPair.create(privateKey));
		String rawTxHex = rlpEncodedRawTxHex(signedRawTxn);
		logger.info("Tx raw hex:" + rawTxHex);

		Key publicKey = BlockchainUtils.recoverPublicKey(rawTxHex);
		Assert.assertNotNull(publicKey);
		String hexAddress = publicKey.getHexAddress();

		RawTransaction newRawTransaction = RLPUtils.decode(rawTxHex);
		newRawTransaction.setSignature(null);

		String txIdHex = BlockchainUtils.generateTransactionId(newRawTransaction,
				Address.fromHexString(hexAddress));
		TransferResult transferResult = TransactionClient.transfer(rawTxHex);
		logger.info("Calculate transaction TxId:" + txIdHex);
		logger.info("Send result:" + JSON.toJSONString(transferResult));
		Assert.assertNotNull(transferResult);
		Assert.assertEquals(txIdHex, transferResult.getId());

	}

	@Test
	public void testDelegatorSignAndTransfer() throws ClientIOException {
		RawTransaction rawTransaction = generatingNativeRawTxn();
		TransactionReserved reserved = new TransactionReserved();
		reserved.setDelegationFeature(true);
		rawTransaction.setReserved(reserved);
		RawTransaction signRawTransaction = TransactionClient.sign(rawTransaction,
				ECKeyPair.create("0x2d7c882bad2a01105e36dda3646693bc1aaaa45b0ed63fb0ce23c060294f3af2"));
		RawTransaction delegatorSignRawTransaction = TransactionClient.delegatorSign(signRawTransaction,
				ECKeyPair.create("0x87e0eba9c86c494d98353800571089f316740b0cb84c9a7cdf2fe5c9997c7966"));

		TransferResult result = TransactionClient.transfer(delegatorSignRawTransaction);
		String txIdHex = BlockchainUtils.generateTransactionId(rawTransaction,
				Address.fromHexString(ECKeyPair.create(
						"0x2d7c882bad2a01105e36dda3646693bc1aaaa45b0ed63fb0ce23c060294f3af2")
						.getAddress()));
		logger.info("Send delegator pay gas Transaction result: " + JSON.toJSONString(result));
		logger.info("Calc txId: " + txIdHex);
	}

	public static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte aByte : bytes) {
			int decimal = (int) aByte & 0xff; // bytes widen to int, need mask, prevent sign extension
								// get last 8 bits
			String hex = Integer.toHexString(decimal);
			if (hex.length() % 2 == 1) { // if half hex, pad with zero, e.g \t
				hex = "0" + hex;
			}
			result.append(hex);
		}
		return result.toString();
	}

	@Test
	public void testDecode() throws ClientIOException {

		String raw = "0xf8c152880160c141438db6528202d0f85ff85d94228ebbee999c6a7ad74a6130e81b12f9fe237ba38001b844a9059cbb000000000000000000000000bf85ef4216340eb5cd3c57b550aae7a2712d48d2000000000000000000000000000000000000000000000000002386f26fc1000080830f424080884f5fdf3bb50274a0c0b841da7e57362f137357fb137a9786e9a10ac1afdc0e0e02ef1528e0758fa48ef2305aa17dfb23bc3b094a5f9345d016eec050f2b878bf870031d5eb5aae375dc18201";

		RawTransaction rawTx = RLPUtils.decode(raw);
		Key key = BlockchainUtils.recoverPublicKey(rawTx);
		String origin = key.getAddress();
		System.out.println("chainTag:");
		System.out.println(rawTx.getChainTag());
		System.out.println("raw Tx");
		System.out.println(rawTx.toString());
		System.out.println("origin:");
		System.out.println(origin);

		RawClause[] rawClauses = rawTx.getClauses();
		for (int i = 0; i < rawClauses.length; i++) {
			RawClause rc = rawClauses[i];
			System.out.println("--- CLAUSE");
			System.out.println(hex(rc.getTo()));
			System.out.println(hex(rc.getValue()));
			System.out.println(hex(rc.getData()));
			System.out.println(hex(rc.getToken()));
			System.out.println("--- END OF CLAUSE ");
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}
}
