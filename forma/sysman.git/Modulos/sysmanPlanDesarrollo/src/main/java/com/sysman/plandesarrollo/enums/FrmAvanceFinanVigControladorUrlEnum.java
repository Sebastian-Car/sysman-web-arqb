package com.sysman.plandesarrollo.enums;

public enum FrmAvanceFinanVigControladorUrlEnum {

	URL001("FRMEJECUCIONPLANDESARROLLOCONTROLADORURL", "4002");

	private final String key;
	private final String value;

	private FrmAvanceFinanVigControladorUrlEnum(String key, String value) {
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
