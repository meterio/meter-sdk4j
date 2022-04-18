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
public class SubscribeSocketTransfers<T> {

	private static Logger logger = LoggerFactory.getLogger(SubscribeSocket.class);

	private static String MTRG_ADDRESS = "0x228ebbee999c6a7ad74a6130e81b12f9fe237ba3";
	private static String TranferMethodAddress = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
	private SubscribingCallback callback;
	@SuppressWarnings("unused")
	private Session session;
	private WebSocketClient client;

	public SubscribeSocketTransfers(WebSocketClient client, SubscribingCallback callback) {
		this.client = client;
		this.callback = callback;
	}

	public void close(int status, String message) {
		logger.info("close: {} {} {}", status, session, message);
		if (this.session != null) {
			CloseStatus closeStatus = new CloseStatus(status, message);
			this.session.close(closeStatus);
		}
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

			

			ValueFilter filter = new ValueFilter() {

				public Object process(Object source, String name, Object value) {
					if (name.equals("address")) {
						return MTRG_ADDRESS;
					}
					return value;
				}
			};




			


		

			Object object = JSON.parse(msg);

			SerializeWriter out = new SerializeWriter();
			JSONSerializer serializer = new JSONSerializer(out);
			serializer.getValueFilters().add(filter);
			
	
			serializer.write(object);
	
			

			MTRGEventSubscribingResponse eventRes = JSON.parseObject(out.toString(), MTRGEventSubscribingResponse.class);
          
			 // check if it's a transfer event
			if (eventRes.getTopics().get(0).toString().equals(TranferMethodAddress)){
				logger.info("TXID : {} ", eventRes.getMeta().getTxID());
				String hexId= eventRes.getMeta().getTxID();
				String sender = eventRes.getMeta().getTxOrigin();
				Transaction transaction = TransactionClient.getTransaction(hexId, false, null);
				String dataHex = transaction.getClauses().get(0).getData();
				
            
            	String recipient = HexUtils.getToAddress(dataHex);
				String amount = HexUtils.getAmount(dataHex);
				eventRes.setAmount(amount);
				eventRes.setSender(sender);
				eventRes.setRecipient(recipient);
				eventRes.setToken(1);
				eventRes.setTopics(null);
				logger.info("Event Response :" + JSON.toJSONString(eventRes));
			    //Object obj = JSONObject.parseObject(eventRes.toString(), callback.responseClass());
			    callback.onSubscribe(eventRes);
			}

			

			//int clauseLength = transaction.getClauses().size();
			// if (clauseLength > 1){
				
			// }
			// for (int i = 0; i < clauseLength; i++){
			// 	String dataHex = transaction.getClauses().get(0).getData();
			// 	Long decimal= Long.parseLong(value, 16);
			// }
			

			
			
				
		
			// logger.info("Data Hex :" + JSON.toJSONString(dataHex));
			// logger.info("Sender :" + JSON.toJSONString(sender));
			// logger.info("Amount :" + JSON.toJSONString(amount));
		    // logger.info("Event Response :" + JSON.toJSONString(eventRes));			

			// String outText = out.toString();
			
			// logger.info("Transaction Hex :" + JSON.toJSONString(transaction));
			// logger.info("Data Hex :" + JSON.toJSONString(dataHex));
			// logger.info("Sender :" + JSON.toJSONString(sender));
			// logger.info("Amount :" + JSON.toJSONString(amount));
		    
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

