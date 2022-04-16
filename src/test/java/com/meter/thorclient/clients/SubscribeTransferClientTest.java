package com.meter.thorclient.clients;


import com.alibaba.fastjson.JSON;
import com.meter.thorclient.base.BaseTest;
import com.meter.thorclient.clients.base.SubscribeSocket;
import com.meter.thorclient.clients.base.SubscribingCallback;
import com.meter.thorclient.core.model.blockchain.TransferSubscribingResponse;

import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;




@RunWith(JUnit4.class)
public class SubscribeTransferClientTest extends BaseTest {

    @Test
    public void testSubscribeTransfer() throws Exception {
        SubscribingCallback<TransferSubscribingResponse> callback = new SubscribingCallback<TransferSubscribingResponse>() {
            @Override
            public void onClose(int statusCode, String reason) {
                logger.info( "on close:" + statusCode + " reason:" + reason );
            }

            @Override
            public void onConnect(Session session) {
                logger.info( "On connect:" + session.toString() );
            }

            @Override
            public Class<TransferSubscribingResponse> responseClass() {
                return TransferSubscribingResponse.class;
            }

            @Override
            public void onSubscribe(TransferSubscribingResponse response) {
                logger.info( "Transfer Response :" + JSON.toJSONString(response) );
            }
        };
        SubscribeSocket socket = SubscribeClient.subscribeTransfer( null,  callback);
        Thread.sleep( 20000 );
        socket.close( 0, "user close" );
    }
    
}
