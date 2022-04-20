package com.meter.sdk.clients;

import com.meter.sdk.clients.base.AbstractClient;
import com.meter.sdk.core.model.blockchain.Block;
import com.meter.sdk.core.model.blockchain.PeerStat;
import com.meter.sdk.core.model.blockchain.PeerStatList;
import com.meter.sdk.core.model.clients.BlockRef;
import com.meter.sdk.core.model.clients.Revision;
import com.meter.sdk.core.model.exception.ClientIOException;
import com.meter.sdk.utils.BlockchainUtils;
import com.meter.sdk.core.model.clients.Revision;
import com.meter.sdk.utils.BytesUtils;

/**
 * Get information of blockchain. It can get block tag, block reference, nodes
 * status of blockchain.
 */
public class BlockchainClient extends AbstractClient {

    /**
     * Get block chain tag. It is the last byte of genesis block id.
     * 
     * @return byte value .
     * @throws ClientIOException if network error.
     */
    public static byte getChainTag() throws ClientIOException {
        Block genesisBlock = BlockClient.getBlock(Revision.create(0));
        if (genesisBlock == null) {
            throw new RuntimeException(" Get Genesis block error");
        }
        String hexId = genesisBlock.getId();
        if (!BlockchainUtils.isId(hexId)) {
            throw new RuntimeException(" Genesis block id is invalid");
        }
        byte[] bytesId = BytesUtils.toByteArray(hexId);
        if (bytesId == null || bytesId.length != 32) {
            throw new RuntimeException(" Genesis block id converted error");
        }

        Byte n = new Byte("82");

        return bytesId[31];
    }

    /**
     * Get status of your accessing nodes on the blockchain.
     * 
     * @return array of {@link PeerStat}
     * @throws ClientIOException network error.
     */
    public static PeerStatList getPeerStatusList() throws ClientIOException {

        return sendGetRequest(Path.GetNodeInfoPath, null, null, PeerStatList.class);
    }

    /**
     * Get block reference from block chain node.
     * 
     * @param revision optional, if set null, it will be the best block, or set
     *                 {@linkplain Revision#BEST Best},
     *                 or specify block number {@linkplain Revision#create(long)
     *                 create(blocknumber)}.
     * @return {@linkplain BlockRef block reference}
     *         throw ClientIOException network error.
     */
    public static BlockRef getBlockRef(Revision revision) throws ClientIOException {
        Block block = BlockClient.getBlock(revision);
        if (block != null) {
            return block.blockRef();
        } else {
            return null;
        }
    }
}
