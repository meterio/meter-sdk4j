package com.meter.sdk.core.model.blockchain;

import java.io.Serializable;

public class RawClause implements Serializable {

	private byte[] to;

	private byte[] value;

	private byte[] data;

	private byte[] token;

	public RawClause() {
		to = new byte[] {};
		value = new byte[] {};
		data = new byte[] {};
	}

	public RawClause(byte[] to, byte[] value, byte[] data, byte[] token) {
		this.to = to;
		this.value = value;
		this.data = data;
		this.token = token;
	}

	public byte[] getTo() {
		return to;
	}

	public void setTo(byte[] to) {
		this.to = to;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}
}
