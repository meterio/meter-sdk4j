package com.meter.sdk.console;

import com.meter.sdk.core.wallet.WalletInfo;
import com.meter.sdk.utils.BytesUtils;
import com.meter.sdk.utils.Prefix;
import com.meter.sdk.utils.WalletUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WalletConsole {

	/**
	 * createWalletToKeystoreFile
	 * 
	 * @param args
	 *             createWallet password path(options)
	 * @throws IOException
	 */
	public static void createWalletToKeystoreFile(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("You have input invalid parameters.");
			System.exit(0);
		}
		WalletInfo walletInfo = WalletUtils.createWallet(args[1]);
		byte[] rawPrivateKey = walletInfo.getKeyPair().getRawPrivateKey();
		String newPrivateKey = BytesUtils.toHexString(rawPrivateKey, Prefix.ZeroLowerX);
		String keyStoreStr = walletInfo.toKeystoreString();
		String path = "." + File.separator + "keystore.json";
		if (args.length > 2) {
			path = args[2];
		}
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		Writer out = new FileWriter(file);
		out.write(keyStoreStr);
		out.close();
		System.out.println("The wallet created successfully and the key store is:");
		System.out.println(keyStoreStr);
		System.out.println("The wallet created successfully and the privateKey is:");
		System.out.println(newPrivateKey);
	}
}
