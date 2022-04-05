package com.meter.thorclient.clients;

import com.meter.thorclient.clients.base.AbstractClient;
import com.meter.thorclient.core.model.blockchain.Block;
import com.meter.thorclient.core.model.clients.Revision;
import com.meter.thorclient.core.model.exception.ClientIOException;

import java.util.HashMap;

public class BlockClient extends AbstractClient{

    /**
     * Get {@link Block} information.
     * @param revision {@link Revision} optional the block revision, can be null.
     * @return Block {@link Block} can be null.
     * @throws ClientIOException
     */
    public static Block getBlock(Revision revision) throws ClientIOException {
        Revision currentRevision = revision;
        if( revision == null){
            currentRevision =  Revision.BEST;
        }
        HashMap<String, String> uriParams = parameters( new String[]{"revision"}, new String[]{currentRevision.toString()} );
        return sendGetRequest( Path.GetBlockPath, uriParams, null, Block.class );
    }
}
