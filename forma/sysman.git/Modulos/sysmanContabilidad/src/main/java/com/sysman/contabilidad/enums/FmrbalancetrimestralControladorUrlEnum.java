package com.sysman.contabilidad.enums;

public enum FmrbalancetrimestralControladorUrlEnum {
	URL001("BALANCETRIMESTRAL11002", "4013"),
	URL002("BALANCETRIMESTRAL11513", "16005"),
	URL003("BALANCETRIMESTRAL12468", "16003");

	private final String key;
	private final String value;

	private FmrbalancetrimestralControladorUrlEnum(String key, String value) {
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
