package com.meter.sdk.clients;

import com.alibaba.fastjson.JSON;
import com.meter.sdk.base.BaseTest;
import com.meter.sdk.clients.base.SubscribeSocket;
import com.meter.sdk.clients.base.SubscribingCallback;

import com.meter.sdk.core.model.blockchain.SysContractSubscribingResponse;
import com.meter.sdk.clients.base.SubscribeSysContractSocket;
import com.meter.sdk.core.model.blockchain.TransferSubscribingResponse;

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
                logger.info("on close:" + statusCode + " reason:" + reason);
            }

            @Override
            public void onConnect(Session session) {
                logger.info("On connect:" + session.toString());
            }

            @Override
            public Class<TransferSubscribingResponse> responseClass() {
                return TransferSubscribingResponse.class;
            }

            @Override
            public void onSubscribe(TransferSubscribingResponse response) {
                logger.info("Transfer Response :" + JSON.toJSONString(response));
            }
        };
        SubscribeSocket socket = SubscribeClient.subscribeTransfer(null, callback);
        Thread.sleep(20000);
        socket.close(0, "user close");
    }

    @Test
    public void testSubscribeSysContractTransfer() throws Exception {
        SubscribingCallback<SysContractSubscribingResponse> callback = new SubscribingCallback<SysContractSubscribingResponse>() {
            @Override
            public void onClose(int statusCode, String reason) {
                logger.info("on close:" + statusCode + " reason:" + reason);
            }

            @Override
            public void onConnect(Session session) {
                logger.info("On connect:" + session.toString());
            }

            @Override
            public Class<SysContractSubscribingResponse> responseClass() {
                return SysContractSubscribingResponse.class;
            }

            @Override
            public void onSubscribe(SysContractSubscribingResponse response) {
                logger.info("Transfer Response :" + JSON.toJSONString(response));
            }
        };
        SubscribeSysContractSocket socket = SubscribeClient.subscribeSysContractTransfer(null, callback);
        Thread.sleep(20000);
        socket.close(0, "user close");
    }
}
