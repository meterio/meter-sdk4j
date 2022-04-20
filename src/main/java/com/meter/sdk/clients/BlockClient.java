package com.meter.sdk.clients;

import com.meter.sdk.clients.base.AbstractClient;
import com.meter.sdk.core.model.blockchain.Block;
import com.meter.sdk.core.model.clients.Revision;
import com.meter.sdk.core.model.exception.ClientIOException;

import java.util.HashMap;

public class BlockClient extends AbstractClient {

    /**
     * Get {@link Block} information.
     * 
     * @param revision {@link Revision} optional the block revision, can be null.
     * @return Block {@link Block} can be null.
     * @throws ClientIOException
     */
    public static Block getBlock(Revision revision) {
        Revision currentRevision = revision;
        if (revision == null) {
            currentRevision = Revision.BEST;
        }
        HashMap<String, String> uriParams = parameters(new String[] { "revision" },
                new String[] { currentRevision.toString() });
        return sendGetRequest(Path.GetBlockPath, uriParams, null, Block.class);
    }
}
