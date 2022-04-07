package com.meter.thorclient.clients.base;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.meter.thorclient.core.model.blockchain.ContractCall;
import com.meter.thorclient.core.model.blockchain.ContractCallResult;
import com.meter.thorclient.core.model.blockchain.NodeProvider;
import com.meter.thorclient.core.model.clients.Address;
import com.meter.thorclient.core.model.clients.Revision;
import com.meter.thorclient.core.model.exception.ClientArgumentException;
import com.meter.thorclient.core.model.exception.ClientIOException;
import com.meter.thorclient.utils.Prefix;
import com.meter.thorclient.utils.StringUtils;
import com.meter.thorclient.utils.URLUtils;

public abstract class AbstractClient {

	private static Logger logger = LoggerFactory.getLogger(AbstractClient.class);

	public enum Path {

		// Accounts

		GetAccountPath("/accounts/{address}"),
		PostContractCallPath("/accounts/{address}"),
		PostDeployContractPath(
				"/accounts"),
		PostAccountCallPath("/account"),
		GetAccountCodePath(
						"/accounts/{address}/code"),

		GetStorageValuePath("/accounts/{address}/storage/{key}"),

		// Transactions
		GetTransactionPath("/transactions/{id}"), GetTransactionReceipt("/transactions/{id}/receipt"),
		PostTransaction("/transactions"),

		// Blocks
		GetBlockPath("/blocks/{revision}"),

		// Events
		PostFilterEventsLogPath("/logs/event"),

		// Transfers
		PostFilterTransferLogPath("/logs/transfer"),

		// Nodes
		GetNodeInfoPath("/node/network/peers"),

		// SubscribeSocket
		GetSubBlockPath("/subscriptions/block"), GetSubEventPath("/subscriptions/event"),
		GetSubTransferPath("/subscriptions/transfer"),;
		private final String value;

		Path(String value) {
			this.value = value;
		}

		public String getPath() {
			return value;
		}

	}

	protected WebSocketClient client = new WebSocketClient();

	static {
		setTimeout(5000);
	}

	private static String rawUrl(Path path) {
		return NodeProvider.getNodeProvider().getProvider() + path.getPath();
	}

	public static void setTimeout(int timeout) {
		try {
			logger.warn("setTimeout:{}", timeout);
			Unirest.shutdown();
		} catch (IOException e) {
			logger.error("Unirest shutdown error", e);
		}
		Unirest.setTimeouts(timeout, timeout);
	}

	/**
	 * Get the request
	 *
	 * @param path        {@link Path}
	 * @param uriParams   uri parameters
	 * @param queryParams query string parameters
	 * @param tClass      the class of result java object.
	 * @param             <T> Type of result java object.
	 * @return response java object, could be null, mean can not find any result.
	 * @throws IOException node is not reachable or request is not valid.
	 */
	public static <T> T sendGetRequest(Path path, HashMap<String, String> uriParams,
			HashMap<String, String> queryParams, Class<T> tClass) throws ClientIOException {
		String rawURL = rawUrl(path);
		String getURL = URLUtils.urlComposite(rawURL, uriParams, queryParams);
		HttpResponse<String> jsonNode = null;
		try {
			jsonNode = com.mashape.unirest.http.Unirest.get(getURL).asString();
		} catch (UnirestException e) {
			throw new ClientIOException(e);
		}
		return parseResult(tClass, jsonNode);

	}

	private static <T> T parseResult(Class<T> tClass, HttpResponse<String> jsonNode) throws ClientIOException {
		int status = jsonNode.getStatus();
		if (status != 200) {
			String exception_msg = "response exception";
			if (status == 400) {
				exception_msg = "bad request";
			} else if (status == 403) {
				exception_msg = "request forbidden";
			}
			ClientIOException clientIOException = new ClientIOException(
					exception_msg + " " + jsonNode.getBody().toString());
			clientIOException.setHttpStatus(status);
			throw clientIOException;
		} else {
			return JSON.parseObject(jsonNode.getBody().toString(), tClass);

		}
	}

	/**
	 * Post the request
	 *
	 * @param path        {@link Path}
	 * @param uriParams   uri parameters
	 * @param queryParams query string parameters
	 * @param tClass      the class of result java object.
	 * @param             <T> Type of result java object.
	 * @return response java object, could be null, mean can not find any result.
	 * @throws ClientIOException http status 4xx means not enough energy amount.
	 */
	public static <T> T sendPostRequest(Path path, HashMap<String, String> uriParams,
			HashMap<String, String> queryParams, Object postBody, Class<T> tClass) throws ClientIOException {
		String rawURL = rawUrl(path);
		String postURL = URLUtils.urlComposite(rawURL, uriParams, queryParams);
		HttpResponse<String> jsonNode = null;
		String postString = JSON.toJSONString(postBody);
		try {
			jsonNode = Unirest.post(postURL).body(postString).asString();
		} catch (UnirestException e) {
			throw new ClientIOException(e);
		}
		return parseResult(tClass, jsonNode);
	}

	/**
	 * Make connection for subscription.
	 * 
	 * @param url      long live connection url.
	 * @param callback {@link SubscribeSocket}
	 * @return {@link SubscribeSocket}
	 * @throws Exception
	 */
	public static SubscribeSocket subscribeSocketConnect(String url, SubscribingCallback<?> callback) throws Exception {
		if (StringUtils.isBlank(url) || callback == null) {
			throw new ClientIOException("Invalid arguments ");
		}
		WebSocketClient client = new WebSocketClient();
		SubscribeSocket subscribeSocket = new SubscribeSocket(client, callback);
		try {
			logger.info("subscribeSocketConnect start connect ... {}", url);
			client.start();
			URI subUri = new URI(url);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			Future<Session> f = client.connect(subscribeSocket, subUri, request);
			logger.info("subscribeSocketConnect end connect...");
			Session s = f.get(10, TimeUnit.SECONDS);
			if (s.isOpen()) {
				logger.info("subscribeSocketConnect success:{}", s.getRemoteAddress().toString());
			} else {
				logger.error("subscribeSocketConnect failed:{}", s.isOpen());
			}
		} catch (Exception e) {
			logger.error("SubscribeSocket error", e);
		} finally {
			if (!subscribeSocket.isConnected()) {
				logger.info("subscribeSocketConnect stop...");
				try {
					subscribeSocket.close(0, "WebSocket can't connect to: " + url);
				} catch (Exception e) {
					logger.error("SubscribeSocket stop error", e);
				}
			}
		}
		return subscribeSocket;
	}

	protected static HashMap<String, String> parameters(String[] keys, String[] values) {
		if (keys == null || values == null || keys.length != values.length) {
			throw ClientArgumentException.exception("Parameters creating failed");
		}

		HashMap<String, String> params = new HashMap<>();
		for (int index = 0; index < keys.length; index++) {
			params.put(keys[index], values[index]);
		}
		return params;
	}

	/**
	 * Call the contract view function or try to run the transaction to see the
	 * gas-used.
	 * 
	 * @param call            {@link ContractCall}
	 * @param contractAddress {@link Address}
	 * @param revision        {@link Revision}
	 * @return {@link ContractCallResult}
	 * @throws ClientIOException network error
	 */
	public static ContractCallResult callContract(ContractCall call, Address contractAddress, Revision revision)
			throws ClientIOException {
		Revision currentRevision = revision;
		if (currentRevision == null) {
			currentRevision = Revision.BEST;
		}

		HashMap<String, String> uriParams = parameters(new String[] { "address" },
				new String[] { contractAddress.toHexString(Prefix.ZeroLowerX) });
		HashMap<String, String> queryParams = parameters(new String[] { "revision" },
				new String[] { currentRevision.toString() });

		return sendPostRequest(Path.PostContractCallPath, uriParams, queryParams, call, ContractCallResult.class);
	}

}
