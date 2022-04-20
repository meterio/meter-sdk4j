package com.meter.sdk.clients;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.base.BaseTest;
import com.meter.sdk.clients.base.SubscribeSocket;
import com.meter.sdk.clients.base.SubscribingCallback;
import com.meter.sdk.core.model.blockchain.EventSubscribingResponse;

import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SubscribeEventClientTest extends BaseTest {

    @Test
    public void testSubscribeEvent() throws Exception {
        SubscribingCallback<EventSubscribingResponse> callback = new SubscribingCallback<EventSubscribingResponse>() {
            @Override
            public void onClose(int statusCode, String reason) {
                logger.info("Closed: " + statusCode);
            }

            @Override
            public void onConnect(Session session) {
                logger.info("Connected");
            }

            @Override
            public Class<EventSubscribingResponse> responseClass() {
                return EventSubscribingResponse.class;
            }

            @Override
            public void onSubscribe(EventSubscribingResponse response) {
                logger.info("Event Response :" + JSON.toJSONString(response));
            }
        };
        SubscribeSocket socket = SubscribeClient.subscribeEvent(null, callback);
        Thread.sleep(20000);
        socket.close(0, "user close");
    }

}
