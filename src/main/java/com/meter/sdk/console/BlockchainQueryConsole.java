package com.meter.sdk.console;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.clients.BlockClient;
import com.meter.sdk.clients.BlockchainClient;
import com.meter.sdk.core.model.blockchain.Block;
import com.meter.sdk.core.model.clients.Revision;

public class BlockchainQueryConsole {

	public static void getBestBlockRef() {
		byte[] blockRefByte = BlockClient.getBlock(Revision.BEST).blockRef().toByteArray();
		String blockRef = ByteUtils.toHexString(blockRefByte);
		System.out.println("BlockRef:");
		System.out.println("0x" + blockRef);
	}

	public static void getBestBlock(String[] args) {
		Block block = null;
		if (args != null && args.length > 2) {
			Revision revision = Revision.create(Long.parseLong(args[2]));
			block = BlockClient.getBlock(revision);
		} else {
			block = BlockClient.getBlock(Revision.BEST);
		}
		System.out.println("Block:");
		System.out.println(JSON.toJSONString(block));
	}

	public static void getChainTag() {
		byte chainTagByte = BlockchainClient.getChainTag();
		String chainTag = String.format("%02x", chainTagByte);
		System.out.println("ChainTag:");
		System.out.println("0x" + chainTag);
	}

}
