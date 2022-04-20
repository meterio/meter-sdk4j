package com.meter.thorclient.clients;

import com.meter.thorclient.core.model.blockchain.ContractCall;
import com.meter.thorclient.core.model.blockchain.ContractCallResult;
import com.meter.thorclient.core.model.blockchain.TransferResult;
import com.meter.thorclient.core.model.clients.*;
import com.meter.thorclient.core.model.clients.base.AbiDefinition;
import com.meter.thorclient.core.model.clients.base.AbstractToken;
import com.meter.thorclient.core.model.exception.ClientArgumentException;
import com.meter.thorclient.core.model.exception.ClientIOException;
import com.meter.thorclient.utils.Prefix;
import com.meter.thorclient.utils.crypto.ECKeyPair;

public class ERC20ContractClient extends TransactionClient {

	/**
	 * Get amount from ERC20 contract.
	 * 
	 * @param address
	 *            address of token holder.
	 * @param token
	 *            {@link ERC20Token} required, the token {@link ERC20Token}
	 * @param revision
	 *            {@link Revision} if it is null, it will fallback to default
	 *            {@link Revision#BEST}
	 * @return {@link Amount}
	 * @throws ClientIOException
	 *             {@link ClientIOException}
	 */
	public static Amount getERC20Balance(Address address, int token, Revision revision)
			throws ClientIOException {
		Address contractAddr = ERC20Token.getContractAddress(token);
		Revision currRevision = revision;
		if (currRevision == null) {
			currRevision = Revision.BEST;
		}
		AbiDefinition abiDefinition = ERC20Contract.defaultERC20Contract.findAbiDefinition("balanceOf");
		ContractCall call = ERC20Contract.buildCall(abiDefinition, address.toHexString(null));
		ContractCallResult contractCallResult = callContract(call, contractAddr, currRevision);
		if (contractCallResult == null) {
			return null;
		}
        
		ERC20Token _token = token == 0 ? ERC20Token.MTR : ERC20Token.MTRG;

		return contractCallResult.getBalance(_token);
	}

	/**
	 * Transfer ERC20 token
	 * 
	 * @param receivers
	 *            {@link Address} array
	 * @param amounts
	 *            {@link Amount} array
	 * @param gas
	 *            gas at least 7000
	 * @param gasCoef
	 *            gas coef
	 * @param expiration
	 *            expiration
	 * @param keyPair
	 *            your private key.
	 * @return {@link TransferResult}
	 * @throws ClientIOException
	 */
	public static TransferResult transferERC20Token(Address[] receivers, Amount[] amounts, int gas, byte gasCoef,
			int expiration, ECKeyPair keyPair, int token) throws ClientIOException {
		if (receivers == null) {
			throw ClientArgumentException.exception("receivers is null");
		}
		
		if (amounts == null) {
			throw ClientArgumentException.exception("amounts is null");
		}
		if (receivers.length != amounts.length) {
			throw ClientArgumentException.exception("receivers length equal to amounts length.");
		}

		AbiDefinition abi = ERC20Contract.defaultERC20Contract.findAbiDefinition("transfer");
		if (abi == null) {
			throw new IllegalArgumentException("Can not find abi master method");
		}
		ToClause[] clauses = new ToClause[receivers.length];
		for (int index = 0; index < receivers.length; index++) {
			
			
			Address contractAddr = ERC20Token.getContractAddress(token);
			
			clauses[index] = ProtoTypeContract.buildToClause(contractAddr, abi,token,
			receivers[index].toHexString(Prefix.ZeroLowerX), amounts[index].toBigInteger());

		}
		return invokeContractMethod(clauses, gas, gasCoef, expiration, keyPair);

	}

}
