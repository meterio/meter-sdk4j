package com.meter.thorclient.core.model.clients;

import com.meter.thorclient.core.model.clients.base.AbstractToken;
import com.meter.thorclient.core.model.exception.ClientArgumentException;
import com.meter.thorclient.utils.StringUtils;

/**
 * The token which is follow the interface of ERC20 protocol.
 */
public final class ERC20Token extends AbstractToken {

	public static final ERC20Token MTRG = new ERC20Token("MTRG", Address.MTRG_Address, 18);
	protected Address contractAddress;

	/**
	 * Create {@link ERC20Token} object.
	 * 
	 * @param name
	 *            token name.
	 * @param address
	 *            {@link Address} address.
	 * @return
	 */
	public static ERC20Token create(String name, Address address, int unit) {
		if (StringUtils.isBlank(name)) {
			throw ClientArgumentException.exception("Address create argument exception.");
		}
		return new ERC20Token(name, address, unit);
	}

	private ERC20Token(String name, Address address, int unit) {
		super(name, unit);
		this.contractAddress = address;
	}

	public Address getContractAddress() {
		return contractAddress;
	}
}
