package com.sysman.bancoproyectos.enums;

public enum InformeplanindicativoControladorUrlEnum {
	
	URL001("INFORMEPLANINDICATIVOCONTROLADORENUM", "4002");

	private final String key;
	private final String value;

	private InformeplanindicativoControladorUrlEnum(String key, String value) {
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
