package com.meter.thorclient.console;

import com.alibaba.fastjson.JSON;
import com.meter.thorclient.clients.AccountClient;
import com.meter.thorclient.clients.TransactionClient;
import com.meter.thorclient.core.model.blockchain.Account;
import com.meter.thorclient.core.model.blockchain.NodeProvider;
import com.meter.thorclient.core.model.blockchain.Receipt;
import com.meter.thorclient.core.model.blockchain.Transaction;
import com.meter.thorclient.core.model.clients.Address;
import com.meter.thorclient.core.model.clients.Revision;
import com.meter.thorclient.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionConsole {

	/**
	 * args: [getTransactionRecipient] [txId] [node url]
	 * 
	 * @param args
	 */
	public static void getTransactionRecipient(String[] args) {

		if (args.length < 3 || StringUtils.isBlank(args[2])) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		String txId = args[1];
		String nodeUrl = args[2];
		if (StringUtils.isBlank(nodeUrl) && !nodeUrl.startsWith("http")) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		NodeProvider nodeProvider = NodeProvider.getNodeProvider();
		nodeProvider.setProvider(nodeUrl);
		nodeProvider.setTimeout(5000);
		Receipt receipt = TransactionClient.getTransactionReceipt(txId, null);
		System.out.println("Receipt:" + JSON.toJSONString(receipt));
	}

	public static void getTransaction(String[] args) {
		if (args.length < 3 || StringUtils.isBlank(args[2])) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		String txId = args[1];
		String nodeUrl = args[2];
		if (StringUtils.isBlank(nodeUrl) && !nodeUrl.startsWith("http")) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		NodeProvider nodeProvider = NodeProvider.getNodeProvider();
		nodeProvider.setProvider(nodeUrl);
		nodeProvider.setTimeout(5000);
		Transaction transaction = TransactionClient.getTransaction(txId, true, null);
		System.out.println("Transaction:" + JSON.toJSONString(transaction));
	}

	public static void sendRawTransaction(String[] args) throws IOException {
		if (args.length < 3) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		String result = ConsoleUtils.sendRawMTRTx(args[2]);
		System.out.println("Send Result:");
		System.out.println(result);
	}

	public static void sendTransactionFromCSVFile(String[] args, String privateKey) throws Exception {
		if (args.length < 4) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		File file = new File(args[3]);
		if (file.isFile()) {
			List<String[]> transactionList = ConsoleUtils.readExcelFile(args[3]);
			String result = ConsoleUtils.doSignMTRTx(transactionList, privateKey, true);
			System.out.println("Send Result:");
			System.out.println(result);
		} else {
			System.out.println("You have input invalid parameters.");
		}
	}

	public static void signMTRTxn(String[] args) throws Exception {
		String privateKey;// args=sign filePath privateKey
		if (args.length < 3 || StringUtils.isBlank(args[2])) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		privateKey = args[2];
		File file = new File(args[1]);
		if (file.isFile()) {
			List<String[]> transactionList = ConsoleUtils.readExcelFile(args[1]);
			String rawTransaction = ConsoleUtils.doSignMTRTx(transactionList, privateKey, false);
			System.out.println("Raw Transaction:");
			System.out.println(rawTransaction);
		} else {
			System.out.println("You have input invalid parameters.");
		}
	}

	public static void signMTRGTxn(String[] args) throws Exception {
		String privateKey;// args=sign filePath privateKey
		if (args.length < 3 || StringUtils.isBlank(args[2])) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		privateKey = args[2];
		File file = new File(args[1]);
		if (file.isFile()) {
			List<String[]> transactionList = ConsoleUtils.readExcelFile(args[1]);
			String rawTransaction = ConsoleUtils.doSignMTRGTx(transactionList, privateKey, false);
			System.out.println("Raw Transaction:");
			System.out.println(rawTransaction);
		} else {
			System.out.println("You have input invalid parameters.");
		}
	}

	/**
	 * transferMTR
	 * 
	 * @param args
	 *            server-url to amount chainTag privateKey
	 * @throws Exception
	 */
	public static void transferMTR(String[] args) throws Exception {
		String privateKey;
		if (args.length < 6) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		privateKey = args[5];

		List<String[]> transactionList = new ArrayList<String[]>();
		String[] tranfs = new String[4];
		tranfs[0] = args[2];
		tranfs[1] = args[3];
		tranfs[2] = args[4];
		tranfs[3] = null;
		transactionList.add(tranfs);
		String result = ConsoleUtils.doSignMTRTx(transactionList, privateKey, true);
		System.out.println(result);

	}

	/**
	 * transferMTR
	 * 
	 * @param args
	 *            server-url to amount chainTag privateKey gaslimit(options)
	 * @throws Exception
	 */
	public static void transferMTRG(String[] args) throws Exception {
		String privateKey;
		if (args.length < 6) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		privateKey = args[5];

		List<String[]> transactionList = new ArrayList<String[]>();
		String[] tranfs = new String[4];
		tranfs[0] = args[2];
		tranfs[1] = args[3];
		tranfs[2] = args[4];
		tranfs[3] = null;
		transactionList.add(tranfs);
		String result = ConsoleUtils.doSignMTRGTx(transactionList, privateKey, true,
				tranfs.length > 6 ? Integer.parseInt(tranfs[6]) : null);
		System.out.println(result);
	}

	public static void getBalance(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		Address address = Address.fromHexString(args[2]);
		Account account = AccountClient.getAccountInfo(address, Revision.BEST);
		System.out.println(JSON.toJSONString(account));
	}
}
