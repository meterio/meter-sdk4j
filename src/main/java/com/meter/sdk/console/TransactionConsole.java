package com.meter.sdk.console;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.clients.AccountClient;
import com.meter.sdk.clients.TransactionClient;
import com.meter.sdk.core.model.blockchain.Account;
import com.meter.sdk.core.model.blockchain.NodeProvider;
import com.meter.sdk.core.model.blockchain.Receipt;
import com.meter.sdk.core.model.blockchain.Transaction;
import com.meter.sdk.core.model.clients.Address;
import com.meter.sdk.core.model.clients.Revision;
import com.meter.sdk.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

enum TokenType {
	MTR,
	MTRG
}

public class TransactionConsole {

	/**
	 * args: [getTransactionRecipient] [txId] [node url]
	 * 
	 * @param args
	 */

	public static boolean validateToken(String token) {
		return token.equals("0") || token.equals("1");

	}

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
		String result = ConsoleUtils.sendRawTx(args[2]);
		System.out.println("Send Result:");
		System.out.println(result);
	}

	public static void sendTransactionFromCSVFile(String[] args, String privateKey) throws Exception {
		if (args.length < 5) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}

		if (!validateToken(args[4])) {
			System.out.println("Token field is invalid. Should 0 for MTR or 1 for MTRG");
			System.exit(0);
		}

		int token = Integer.parseInt(args[4]);
		File file = new File(args[3]);
		if (file.isFile()) {
			List<String[]> transactionList = ConsoleUtils.readExcelFile(args[3]);

			String result = ConsoleUtils.doSignNativeTx(transactionList, token, privateKey, true);
			System.out.println("Send Result:");
			System.out.println(result);
		} else {
			System.out.println("You have input invalid parameters.");
		}
	}

	public static void signNativeTxn(String[] args) throws Exception {
		String privateKey;// args=sign filePath privateKey token
		for (int i = 0; i < args.length; i++) {
			System.out.print(i);
			System.out.print(":");
			System.out.println(args[i]);
		}
		System.out.println(args.length < 4);
		System.out.println(StringUtils.isBlank(args[2]));
		System.out.println(args.length < 4 || StringUtils.isBlank(args[2]));
		if (args.length < 4 || StringUtils.isBlank(args[2])) {
			System.out.println("IMHERE");
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}

		if (!validateToken(args[3])) {
			System.out.println("Token field is invalid. Should 0 for MTR or 1 for MTRG");
			System.exit(0);
		}

		int token = Integer.parseInt(args[3]);
		privateKey = args[4];
		File file = new File(args[2]);
		if (file.isFile()) {
			List<String[]> transactionList = ConsoleUtils.readExcelFile(args[2]);
			System.out.println("Read excel file");
			String rawTransaction = ConsoleUtils.doSignNativeTx(transactionList, token, privateKey, false);
			System.out.println("Raw Transaction:");
			System.out.println(rawTransaction);
		} else {
			System.out.println("You have input invalid parameters.");
		}
	}

	public static void signERC20Txn(String[] args) throws Exception {
		String privateKey;// args=sign filePath privateKey
		if (args.length < 4 || StringUtils.isBlank(args[2])) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}

		if (!validateToken(args[3])) {
			System.out.println("Token field is invalid. Should 0 for MTR or 1 for MTRG");
			System.exit(0);
		}
		int token = Integer.parseInt(args[3]);

		privateKey = args[2];
		File file = new File(args[1]);

		if (file.isFile()) {
			List<String[]> transactionList = ConsoleUtils.readExcelFile(args[1]);
			String rawTransaction = ConsoleUtils.doSignERC20Tx(transactionList, token, privateKey, false);
			System.out.println("Raw Transaction:");
			System.out.println(rawTransaction);
		} else {
			System.out.println("You have input invalid parameters.");
		}
	}

	/**
	 * transferNative
	 * 
	 * @param args
	 *             server-url to amount chainTag privateKey
	 * @throws Exception
	 */
	public static void transferNative(String[] args) throws Exception {
		String privateKey;
		if (args.length < 7) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}

		if (!validateToken(args[6])) {
			System.out.println("Token field is invalid. Should 0 for MTR or 1 for MTRG");
			System.exit(0);
		}

		int token = Integer.parseInt(args[6]);
		privateKey = args[5];

		List<String[]> transactionList = new ArrayList<String[]>();
		String[] tranfs = new String[4];
		tranfs[0] = args[2];
		tranfs[1] = args[3];
		tranfs[2] = args[4];
		tranfs[3] = null;
		transactionList.add(tranfs);
		String result = ConsoleUtils.doSignNativeTx(transactionList, token, privateKey, true);
		System.out.println(result);

	}

	/**
	 * transferNative
	 * 
	 * @param args
	 *             server-url to amount chainTag privateKey gaslimit(options)
	 * @throws Exception
	 */
	public static void transferERC20(String[] args) throws Exception {
		String privateKey;
		if (args.length < 7) {
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

		if (!validateToken(args[6])) {
			System.out.println("Token field is invalid. Should 0 for MTR or 1 for MTRG");
			System.exit(0);
		}

		int token = Integer.parseInt(args[6]);
		transactionList.add(tranfs);
		String result = ConsoleUtils.doSignERC20Tx(transactionList, token, privateKey, true,
				null);
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
