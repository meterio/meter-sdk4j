package com.meter.sdk.clients;

import java.util.HashMap;

import com.meter.sdk.clients.base.AbstractClient;
import com.meter.sdk.core.model.blockchain.Receipt;
import com.meter.sdk.core.model.blockchain.Transaction;
import com.meter.sdk.core.model.blockchain.TransferRequest;
import com.meter.sdk.core.model.blockchain.TransferResult;
import com.meter.sdk.core.model.clients.Address;
import com.meter.sdk.core.model.clients.Amount;
import com.meter.sdk.core.model.clients.BlockRef;
import com.meter.sdk.core.model.clients.RawTransaction;
import com.meter.sdk.core.model.clients.Revision;
import com.meter.sdk.core.model.clients.ToClause;
import com.meter.sdk.core.model.clients.ToData;
import com.meter.sdk.core.model.clients.Token;
import com.meter.sdk.core.model.exception.ClientArgumentException;
import com.meter.sdk.core.model.exception.ClientIOException;
import com.meter.sdk.utils.BlockchainUtils;
import com.meter.sdk.utils.BytesUtils;
import com.meter.sdk.utils.CryptoUtils;
import com.meter.sdk.utils.Prefix;
import com.meter.sdk.utils.RawTransactionFactory;
import com.meter.sdk.utils.StringUtils;
import com.meter.sdk.utils.crypto.ECDSASign;
import com.meter.sdk.utils.crypto.ECKeyPair;

public class TransactionClient extends AbstractClient {

	public final static int ContractGasLimit = 30000;

	/**
	 * Get transaction by transaction Id.
	 * 
	 * @param txId     required transaction id .
	 * @param isRaw    is response raw transaction.
	 * @param revision {@link Revision} revision.
	 * @return Transaction {@link Transaction}
	 * @throws ClientIOException
	 */
	public static Transaction getTransaction(String txId, boolean isRaw, Revision revision)
			throws ClientIOException {
		if (!BlockchainUtils.isId(txId)) {
			throw ClientArgumentException.exception("Tx id is invalid");
		}
		Revision currRevision = revision;
		if (currRevision != null && currRevision.toString().equals(Revision.BEST.toString())) {
			currRevision = null;
		}
		HashMap<String, String> uriParams = parameters(new String[] { "id" }, new String[] { txId });
		HashMap<String, String> queryParams = parameters(new String[] { "head", "raw" },
				new String[] { currRevision == null ? null : currRevision.toString(),
						Boolean.toString(isRaw) });
		return sendGetRequest(Path.GetTransactionPath, uriParams, queryParams, Transaction.class);
	}

	/**
	 * Get transaction receipt
	 * 
	 * @param txId     txId hex string start with "0x"
	 * @param revision {@link Revision}
	 * @return {@link Receipt} return Receipt .
	 * @throws ClientIOException
	 */
	public static Receipt getTransactionReceipt(String txId, Revision revision) throws ClientIOException {
		if (!BlockchainUtils.isId(txId)) {
			throw ClientArgumentException.exception("Tx id is invalid");
		}
		Revision currRevision = revision;
		if (currRevision != null && currRevision.toString().equals(Revision.BEST.toString())) {
			currRevision = null;
		}
		HashMap<String, String> uriParams = parameters(new String[] { "id" }, new String[] { txId });
		return sendGetRequest(Path.GetTransactionReceipt, uriParams, currRevision == null ? null
				: parameters(new String[] { "head" }, new String[] { currRevision.toString() }),
				Receipt.class);
	}

	/**
	 * Transfer amount, raw transaction will be encoded by rlp encoder and convert
	 * to hex string with prefix "0x". Then the hex string will be packaged into
	 * {@link TransferRequest} bean object and serialized to JSON string.
	 * 
	 * @param transaction {@link RawTransaction} raw transaction to to send
	 * @return {@link TransferResult}
	 * @throws ClientIOException network error, 5xx http status means request error,
	 *                           4xx http status means no enough gas or balance.
	 */
	public static TransferResult transfer(final RawTransaction transaction) throws ClientIOException {
		if (transaction == null || transaction.getSignature() == null) {
			ClientArgumentException.exception("Raw transaction is invalid");
		}
		byte[] rawBytes = transaction.encode();
		if (rawBytes == null) {
			throw ClientArgumentException.exception("Raw transaction is encode error");
		}
		String hexRaw = BytesUtils.toHexString(rawBytes, Prefix.ZeroLowerX);
		TransferRequest request = new TransferRequest();
		request.setRaw(hexRaw);
		return sendPostRequest(Path.PostTransaction, null, null, request, TransferResult.class);
	}

	/**
	 * Send the transaction hex string.
	 * 
	 * @param rawTransactionHexString hex string of raw transaction.
	 * @return {@link TransferResult}
	 * @throws ClientIOException
	 */
	public static TransferResult transfer(final String rawTransactionHexString) throws ClientIOException {

		if (!StringUtils.isHex(rawTransactionHexString)) {
			throw ClientArgumentException.exception("Raw transaction is encode error");
		}
		TransferRequest request = new TransferRequest();
		request.setRaw(rawTransactionHexString);
		return sendPostRequest(Path.PostTransaction, null, null, request, TransferResult.class);
	}

	/**
	 * Send transaction bytes.
	 * 
	 * @param rawTransactionBytes byte array.
	 * @return {@link TransferResult}
	 * @throws ClientIOException
	 */
	public static TransferResult transfer(final byte[] rawTransactionBytes) throws ClientIOException {
		if (rawTransactionBytes == null) {
			throw ClientArgumentException.exception("rawTransaction byte array is null.");
		}
		String hexString = BytesUtils.toHexString(rawTransactionBytes, Prefix.ZeroLowerX);
		return transfer(hexString);
	}

	/**
	 * Sign the raw transaction.
	 * 
	 * @param rawTransaction {@link RawTransaction}
	 * @return {@link RawTransaction} with signature.
	 */
	public static RawTransaction sign(RawTransaction rawTransaction, ECKeyPair keyPair) {
		if (rawTransaction == null || keyPair == null) {
			throw ClientArgumentException.exception("raw transaction object is invalid.");
		}
		ECDSASign.SignatureData signature = ECDSASign.signMessage(rawTransaction.encode(), keyPair, true);
		byte[] signBytes = signature.toByteArray();
		rawTransaction.setSignature(signBytes);
		return rawTransaction;
	}

	/**
	 * Delegator signs the transaction.
	 * 
	 * @param rawTransaction {@link RawTransaction}
	 * @param keyPair
	 * @return
	 */
	public static RawTransaction delegatorSign(RawTransaction rawTransaction, ECKeyPair keyPair) {
		if (rawTransaction == null || keyPair == null) {
			throw ClientArgumentException.exception("raw transaction object or keyPair is invalid.");
		}
		if (!rawTransaction.getReserved().isDelegationFeature()) {
			throw ClientArgumentException
					.exception("raw transaction has no delegation feature, the raw transaction is invalid.");
		}
		byte[] delegatorSigningHash = BlockchainUtils.delegatorSigningHash(rawTransaction);
		ECDSASign.SignatureData signatureData = ECDSASign.signMessage(delegatorSigningHash, keyPair, false);
		byte[] delegationSignature = signatureData.toByteArray();
		if (delegationSignature != null) {
			byte[] signature = rawTransaction.getSignature();
			byte[] concatenatingSignature = BlockchainUtils.concatenateSignature(signature,
					delegationSignature);
			rawTransaction.setSignature(concatenatingSignature);
			return rawTransaction;
		} else {
			return null;
		}

	}

	/**
	 * Sign and transfer the raw transaction.
	 * 
	 * @param rawTransaction {@link RawTransaction} raw transaction without
	 *                       signature data
	 * @param keyPair        {@link ECKeyPair} the key pair for the private key.
	 * @return {@link TransferResult}
	 * @throws ClientIOException
	 */
	public static TransferResult signThenTransfer(RawTransaction rawTransaction, ECKeyPair keyPair)
			throws ClientIOException {
		RawTransaction signedRawTxn = sign(rawTransaction, keyPair);
		return transfer(signedRawTxn);
	}

	/**
	 * Build a transaction clause
	 * 
	 * @param toAddress {@link Address} destination address.
	 * @param amount    {@link Amount} amount to transfer.
	 * @param data      {@link ToData} some comments maybe.
	 * @return {@link ToClause} to clause.
	 */
	public static ToClause buildTransferToClause(Address toAddress, Amount amount, ToData data, int token) {

		if (toAddress == null) {
			throw ClientArgumentException.exception("toAddress is null");
		}
		if (amount == null) {
			throw ClientArgumentException.exception("amount is null");
		}
		if (data == null) {
			throw ClientArgumentException.exception("data is null");
		}

		if (token != 0 && token != 1) {
			throw ClientArgumentException.exception("specify a valid token - 0 or 1");
		}

		Token token_ = token == 0 ? Token.fromHexString("0x") : Token.fromHexString("0x1");

		return new ToClause(toAddress, amount, data, token_);
	}

	/**
	 * Build deploying the contract codes.
	 * 
	 * @param contractHex byte array
	 * @return
	 */
	public static ToClause buildDeployClause(String contractHex) {
		if (!StringUtils.isHex(contractHex)) {
			return null;
		}
		ToData toData = new ToData();
		toData.setData(contractHex);
		Token token = Token.fromHexString("0x");
		return new ToClause(Address.NULL_ADDRESS, Amount.ZERO, toData, token);
	}

	/**
	 * Deploy a contract to the block chain.
	 * 
	 * @param contractHex the contract hex string with
	 * @param gas         the gas
	 * @param gasCoef     the gas coefficient
	 * @param expiration  the expiration
	 * @param keyPair     private keypair
	 */
	public static TransferResult deployContract(String contractHex, int gas, byte gasCoef, int expiration,
			ECKeyPair keyPair) {
		ToClause toClause = buildDeployClause(contractHex);
		if (toClause == null) {
			throw ClientArgumentException.exception("The contract hex string is null");
		}
		ToClause[] toClauses = new ToClause[1];
		toClauses[0] = toClause;
		return invokeContractMethod(toClauses, gas, gasCoef, expiration, keyPair);
	}

	/**
	 * InvokeContractMethod send transaction to contract.
	 * 
	 * @param toClauses  to-clauses array.
	 * @param gas
	 * @param gasCoef
	 * @param expiration
	 * @param keyPair
	 * @return
	 * @throws ClientIOException
	 */
	protected static TransferResult invokeContractMethod(ToClause[] toClauses, int gas, byte gasCoef,
			int expiration,
			ECKeyPair keyPair) throws ClientIOException {

		if (keyPair == null) {
			throw ClientArgumentException.exception("ECKeyPair is null.");
		}

		if (gas < ContractGasLimit) {
			throw ClientArgumentException.exception("gas is too small.");
		}
		if (gasCoef < 0) {
			throw ClientArgumentException.exception("gas coef is too small.");
		}

		if (expiration <= 0) {
			throw ClientArgumentException.exception("expiration is invalid.");
		}

		if (toClauses == null) {
			throw ClientArgumentException.exception("To clause is null");
		}

		byte chainTag = BlockchainClient.getChainTag();
		BlockRef bestRef = BlockchainClient.getBlockRef(null);
		if (bestRef == null || chainTag == 0) {
			throw new ClientIOException("Get chainTag: " + chainTag + " BlockRef: " + bestRef);
		}
		RawTransaction rawTransaction = RawTransactionFactory.getInstance().createRawTransaction(chainTag,
				bestRef.toByteArray(), expiration, gas, gasCoef, CryptoUtils.generateTxNonce(),
				toClauses);
		return TransactionClient.signThenTransfer(rawTransaction, keyPair);
	}

}
