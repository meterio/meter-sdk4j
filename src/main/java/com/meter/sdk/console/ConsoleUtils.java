package com.meter.sdk.console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.clients.BlockchainClient;
import com.meter.sdk.clients.TransactionClient;
import com.meter.sdk.core.model.blockchain.TransferResult;
import com.meter.sdk.core.model.clients.Token;
import com.meter.sdk.core.model.clients.Address;
import com.meter.sdk.core.model.clients.Amount;
import com.meter.sdk.core.model.clients.ERC20Contract;
import com.meter.sdk.core.model.clients.ERC20Token;
import com.meter.sdk.core.model.clients.RawTransaction;
import com.meter.sdk.core.model.clients.Revision;
import com.meter.sdk.core.model.clients.ToClause;
import com.meter.sdk.core.model.clients.ToData;
import com.meter.sdk.core.model.clients.base.AbstractToken;
import com.meter.sdk.utils.BytesUtils;
import com.meter.sdk.utils.CryptoUtils;
import com.meter.sdk.utils.Prefix;
import com.meter.sdk.utils.RawTransactionFactory;
import com.meter.sdk.utils.StringUtils;
import com.meter.sdk.utils.crypto.ECKeyPair;

public class ConsoleUtils {

	public static String doSignNativeTx(List<String[]> transactions, int token, String privateKey, boolean isSend)
			throws IOException {

		byte chainTag = 0;
		byte[] blockRef = null;

		List<ToClause> clauses = new ArrayList<ToClause>();
		for (String[] transaction : transactions) {
			Amount amount = Amount.createFromToken(AbstractToken.getToken(token));
			amount.setDecimalAmount(transaction[1]);
			clauses.add(TransactionClient.buildTransferToClause(Address.fromHexString(transaction[0]),
					amount, ToData.ZERO, token));
			chainTag = BytesUtils.toByteArray(transaction[2])[0];
			if (transaction[3] == null) {
				blockRef = BlockchainClient.getBlockRef(null).toByteArray();
			} else {
				blockRef = BytesUtils.toByteArray(transaction[3]);
			}
		}
		int gas = clauses.size() * 30000;
		RawTransaction rawTransaction = RawTransactionFactory.getInstance().createRawTransaction(chainTag,
				blockRef,
				720, gas, (byte) 0x0, CryptoUtils.generateTxNonce(), clauses.toArray(new ToClause[0]));
		if (isSend) {
			TransferResult result = TransactionClient.signThenTransfer(rawTransaction,
					ECKeyPair.create(privateKey));
			return JSON.toJSONString(result);
		} else {
			RawTransaction result = TransactionClient.sign(rawTransaction, ECKeyPair.create(privateKey));
			return BytesUtils.toHexString(result.encode(), Prefix.ZeroLowerX);
		}
	}

	public static String sendRawTx(String rawTransaction) throws IOException {
		TransferResult result = TransactionClient.transfer(rawTransaction);
		return JSON.toJSONString(result);
	}

	public static List<String[]> readExcelFile(String fiePath) throws Exception {

		List<String[]> results = new ArrayList<String[]>();

		Workbook workbook = WorkbookFactory.create(new File(fiePath));
		Sheet sheet = workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		sheet.forEach(row -> {
			int rowNum = row.getRowNum();
			System.out.println("row num:");
			System.out.println(rowNum);
			if (rowNum > 1) {
				int length = row.getLastCellNum();
				List<String> rowData = new ArrayList<String>(length);
				row.forEach(cell -> {
					String cellValue = dataFormatter.formatCellValue(cell);
					if (!StringUtils.isBlank(cellValue)) {
						rowData.add(cellValue);
					}
				});
				if (!rowData.isEmpty()) {
					results.add(rowData.toArray(new String[0]));
				}
			}
		});
		workbook.close();
		System.out.println("READ FILE");
		return results;
	}

	public static String doSignERC20Tx(List<String[]> transactions, int token, String privateKey, boolean isSend)
			throws IOException {
		return doSignERC20Tx(transactions, token, privateKey, isSend, null);
	}

	public static String doSignERC20Tx(List<String[]> transactions, int token, String privateKey, boolean isSend,
			Integer gasLimit)
			throws IOException {

		byte chainTag = 0;
		byte[] blockRef = null;

		List<ToClause> clauses = new ArrayList<ToClause>();
		for (String[] transaction : transactions) {
			Amount amount = Amount.ERC20Amount(token);
			amount.setDecimalAmount(transaction[1]);
			clauses.add(
					ERC20Contract.buildERC20TranferToClause(Address.fromHexString(transaction[0]),
							amount, token));
			chainTag = BytesUtils.toByteArray(transaction[2])[0];
			if (transaction[3] == null) {
				blockRef = BlockchainClient.getBlockRef(null).toByteArray();
			} else {
				blockRef = BytesUtils.toByteArray(transaction[3]);
			}
		}
		if (gasLimit == null) {
			gasLimit = 80000;
		}
		int gas = clauses.size() * gasLimit;
		RawTransaction rawTransaction = RawTransactionFactory.getInstance().createRawTransaction(chainTag,
				blockRef,
				720, gas, (byte) 0x0, CryptoUtils.generateTxNonce(), clauses.toArray(new ToClause[0]));
		if (isSend) {
			TransferResult result = TransactionClient.signThenTransfer(rawTransaction,
					ECKeyPair.create(privateKey));
			return JSON.toJSONString(result);
		} else {
			RawTransaction result = TransactionClient.sign(rawTransaction, ECKeyPair.create(privateKey));
			return BytesUtils.toHexString(result.encode(), Prefix.ZeroLowerX);
		}
	}
}
