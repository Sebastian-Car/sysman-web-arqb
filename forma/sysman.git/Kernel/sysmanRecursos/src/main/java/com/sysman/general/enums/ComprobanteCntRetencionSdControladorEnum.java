package com.sysman.general.enums;

public enum ComprobanteCntRetencionSdControladorEnum {

	PARAM2("TIPO"),

	PARAM1("ANO"),

	PARAM0("COMPANIA");

	private final String value;

	private ComprobanteCntRetencionSdControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
