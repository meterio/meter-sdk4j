package com.meter.thorclient.clients.base;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.meter.thorclient.clients.TransactionClient;
import com.meter.thorclient.core.model.blockchain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import com.meter.thorclient.utils.HexUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.ValueFilter;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;



@WebSocket(maxTextMessageSize = 16 * 1024 * 1024)
public class SubscribeSysContractSocket<T> {

	private static Logger logger = LoggerFactory.getLogger(SubscribeSocket.class);

	private static String MTRG_SYS_CONTRACT_ADDRESS = "0x228ebbee999c6a7ad74a6130e81b12f9fe237ba3";
	private static String MTR_SYS_CONTRACT_ADDRESS = "0x687a6294d0d6d63e751a059bf1ca68e4ae7b13e2";
	private static String TRANSFER_METHOD_ID = "0xa9059cbb";
	private static String TRANSFER_METHOD_TOPIC = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

	private SubscribingCallback callback;
	
	@SuppressWarnings("unused")
	private Session session;
	private WebSocketClient client;

	public SubscribeSysContractSocket(WebSocketClient client, SubscribingCallback callback) {
		this.client = client;
		this.callback = callback;
	}

	public void close(int status, String message) {
		// logger.info("close: {} {} {}", status, session, message);
		// if (this.session != null) {
		// 	CloseStatus closeStatus = new CloseStatus(status, message);
		// 	this.session.close(closeStatus);
		// }
		clean();
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		logger.info("Connection closed: {} {}", statusCode, reason);
		if (this.callback != null) {
			this.callback.onClose(statusCode, reason);
		}
		clean();
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.info("Got connect: {} ", session);
		this.session = session;
		if (this.callback != null) {
			this.callback.onConnect(session);
		}
	}
    


	@OnWebSocketMessage
	public void onMessage(String msg) {
		if (this.callback != null) {
			logger.info("onMessage: {} ", msg);

			SysContractSubscribingResponse eventRes = JSON.parseObject(msg.toString(), SysContractSubscribingResponse.class);
          
			 // check if it's a transfer event
			if (eventRes.getTopics().get(0).toString().equals(TRANSFER_METHOD_TOPIC)){
				
				String hexId= eventRes.getMeta().getTxID();
				String sender = eventRes.getMeta().getTxOrigin();
				
				Transaction transaction = TransactionClient.getTransaction(hexId, false, null);
				String dataHex = transaction.getClauses().get(0).getData();
			

			
				String methodID = dataHex.substring(0,10);
				
				if (methodID.equals(TRANSFER_METHOD_ID)){
            	String recipient = HexUtils.getToAddress(dataHex);
				String amount = HexUtils.getAmount(dataHex);
				eventRes.setAmount(amount);
				eventRes.setSender(sender);
				eventRes.setRecipient(recipient);

				
				if (eventRes.getAddress().equals(MTRG_SYS_CONTRACT_ADDRESS)){
					eventRes.setToken( 1);
				}else{
					eventRes.setToken( 0);
				}
				
				eventRes.setAddress(null);
				eventRes.setTopics(null);
				
			    //Object obj = JSONObject.parseObject(eventRes.toString(), callback.responseClass());
			    callback.onSubscribe(eventRes);
			    }
			}
		}
			
		
		    
		
	}

	public boolean isConnected() {
		return this.session != null;
	}

	private void clean() {
		this.callback = null;
		this.session = null;
		if (this.client != null) {
			try {
				logger.info("client closed...");
				this.client.stop();
				logger.info("client closed success");
			} catch (Exception e) {
				logger.error("WebSocketClient close error", e);
			} finally {
				this.client = null;
			}
		}
	}
}

