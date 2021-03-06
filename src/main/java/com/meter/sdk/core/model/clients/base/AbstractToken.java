package com.meter.sdk.core.model.clients.base;

import java.math.BigDecimal;

/**
 * AbstractToken amount class.
 */
public class AbstractToken {

	protected String name;
	protected BigDecimal precision;
	protected BigDecimal scale;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrecision() {
		return precision;
	}

	public void setPrecision(BigDecimal precision) {
		this.precision = precision;
	}

	public BigDecimal getScale() {
		return scale;
	}

	public void setScale(BigDecimal scale) {
		this.scale = scale;
	}

	// protected AbstractToken(String name) {
	// this.name = name;
	// this.precision = BigDecimal.valueOf(18);
	// this.scale = BigDecimal.valueOf(8);
	// }

	protected AbstractToken(String name, int unit) {
		this.name = name;
		this.precision = BigDecimal.valueOf(unit);
		this.scale = BigDecimal.valueOf(unit);
	}

	public static AbstractToken getToken(int token) {

		if (token == 0) {
			return new AbstractToken("MTR", 18);
		}

		return new AbstractToken("MTRG", 18);

	}

}
