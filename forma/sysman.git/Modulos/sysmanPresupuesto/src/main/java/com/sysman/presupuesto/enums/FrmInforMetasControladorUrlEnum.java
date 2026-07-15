package com.sysman.presupuesto.enums;

public enum FrmInforMetasControladorUrlEnum {
	

	URL7001("FRMINFORMETASCONTROLADORURL7001","7001"),

	URL4007("FRMINFORMETASCONTROLADOR4007","4007"),
	
	URL1884003("FRMINFORMETASCONTROLADOR1884003","1884003");

	private final String key;
	private final String value;

	private FrmInforMetasControladorUrlEnum(String key, String value) {
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
