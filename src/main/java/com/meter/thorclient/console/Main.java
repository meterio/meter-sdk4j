
package com.meter.thorclient.console;

import com.meter.thorclient.core.model.blockchain.NodeProvider;
import com.meter.thorclient.utils.StringUtils;

public class Main {

	private static final String SIGN_NATIVE = "signNative";

	private static final String CREATE_WALLET = "createWallet";

	private static final String SEND = "signAndSend";

	private static final String TRANSFER_NATIVE = "transferNative";

	private static final String TRANSFER_ERC20 = "transferERC20";

	private static final String CHAIN_TAG = "getChainTag";

	private static final String BLOCK_REF = "getBlockRef";

	private static final String GET_BLOCK = "getBlock";

	private static final String GET_TRANSACTION = "getTransaction";

	private static final String GET_TRANSACTION_RECEIPT = "getTransactionReceipt";

	private static final String SEND_RAW = "sendRaw";

	private static final String SIGN_ERC20 = "signERC20";

	private static final String PARSE = "parse";

	private static final String BALANCE = "balance";

	private static final String SUBCRIBE_EVENTS = "subscribeEvent";

	private static final String SUBCRIBE_TRANSFERS = "subscribeTransfer";

	private static final String SUBCRIBE_SYS_CONTRACT_TRANSFERS = "subscribeSysContractTransfer";

	public static void main(String[] args) throws Exception {

		try {
			if (args.length == 0) {
				System.out.println("No arguments specified");
				System.exit(0);
			}

			String privateKey = processConsoleArguments(args);
			if (args[0].equals(GET_TRANSACTION)) {
				// args=getTransaction txId providerUrl
				TransactionConsole.getTransaction(args);
			} else if (args[0].equals(GET_TRANSACTION_RECEIPT)) {
				TransactionConsole.getTransactionRecipient(args);
			} else if (args[0].equals(SIGN_NATIVE)) {
				TransactionConsole.signNativeTxn(args);
			} else if (args[0].equals(SIGN_ERC20)) {
				TransactionConsole.signERC20Txn(args);
			} else if (args[0].equals(TRANSFER_NATIVE)) {
				TransactionConsole.transferNative(args);
			} else if (args[0].equals(TRANSFER_ERC20)) {
				TransactionConsole.transferERC20(args);
			} else if (args[0].equals(BALANCE)) {
				TransactionConsole.getBalance(args);
			} else if (args[0].equals(CHAIN_TAG)) {
				BlockchainQueryConsole.getChainTag();
			} else if (args[0].equals(GET_BLOCK)) {
				BlockchainQueryConsole.getBestBlock(args);
			} else if (args[0].equals(BLOCK_REF)) {
				BlockchainQueryConsole.getBestBlockRef();
			} else if (args[0].equals(CREATE_WALLET)) {
				WalletConsole.createWalletToKeystoreFile(args);
			} else if (args[0].equals(SEND)) {
				// args=signAndSendMTR {providerUrl} {privateKey} {filePath}
				TransactionConsole.sendTransactionFromCSVFile(args, privateKey);
			} else if (args[0].equals(SEND_RAW)) {
				// args=sendMTRRaw {providerUrl} {rawTransaction}
				TransactionConsole.sendRawTransaction(args);
			} else if (args[0].equals(PARSE)) {
				ParserConsole.parse(args);
			} else if (args[0].equals(SUBCRIBE_EVENTS)) {
				// events subscription
				SubscribeClientConsole.subcribeEvent();
			} else if (args[0].equals(SUBCRIBE_TRANSFERS)) {
				// transfers subscription
				SubscribeClientConsole.subcribeTransfer();
			} else if (args[0].equals(SUBCRIBE_SYS_CONTRACT_TRANSFERS)) {
				// transfers subscription
				SubscribeClientConsole.subscribeSysContractTransfer();
			} else {
				System.out.println("Incorrect Command");
			}
		} catch (Exception e) {
			System.out.println("Incorrect command " + e.getMessage());
		}
	}

	/**
	 * Process console input arguments
	 * 
	 * @param args
	 *             input arguments
	 * @return
	 */
	private static String processConsoleArguments(String[] args) {
		String privateKey = null;
		String nodeProviderUrl = null;
		String wsProviderUrl = null;
		if (args[0].equals(CHAIN_TAG) || args[0].equals(BLOCK_REF) || args[0].equals(GET_BLOCK)
				|| args[0].equals(SEND)
				|| args[0].equals(SEND_RAW) || args[0].equals(TRANSFER_NATIVE)
				|| args[0].equals(TRANSFER_ERC20)
				|| args[0].equals(BALANCE)) {

			if (args.length > 1 && !StringUtils.isBlank(args[1]) && args[1].startsWith("http")) {
				nodeProviderUrl = args[1];
			}
			if (StringUtils.isBlank(nodeProviderUrl)) {
				System.out.println("You have input invalid parameters.");
				System.exit(0);
			}
			if (args.length > 2 && args[0].equals(SEND)) {
				// args=send {providerUrl} {privateKey}
				if (!StringUtils.isBlank(args[2])) {
					privateKey = args[2];
				}
				if (StringUtils.isBlank(privateKey)) {
					System.out.println("You have input invalid parameters.");
					System.exit(0);
				}
			}
			NodeProvider nodeProvider = NodeProvider.getNodeProvider();
			nodeProvider.setProvider(nodeProviderUrl);
			nodeProvider.setSocketTimeout(5000);
		}

		if (args[0].equals(SUBCRIBE_EVENTS) || args[0].equals(SUBCRIBE_TRANSFERS)
				|| args[0].equals(SUBCRIBE_SYS_CONTRACT_TRANSFERS)) {
			NodeProvider nodeProvider = NodeProvider.getNodeProvider();
			if (args.length > 1 && !StringUtils.isBlank(args[1]) && args[1].startsWith("ws")) {
				wsProviderUrl = args[1];
			}
			if (StringUtils.isBlank(wsProviderUrl)) {
				System.out.println("You have input invalid parameters.");
				System.exit(0);
			}
			nodeProvider.setWsProvider(wsProviderUrl);
			nodeProvider.setSocketTimeout(5000);
		}
		return privateKey;
	}

}
