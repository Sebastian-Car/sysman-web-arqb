package com.sysman.plandesarrollo.enums;

public enum FrmejecucionplandesarrolloControladorUrlEnum {

	URL001("FRMEJECUCIONPLANDESARROLLOCONTROLADORURL", "4002"),
	
	URL002("FRMEJECUCIONPLANDESARROLLOCONTROLADORURL", "552059");

	private final String key;
	private final String value;

	private FrmejecucionplandesarrolloControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
