package com.sysman.nomina.enums;

public enum PorcentajesFspAdiControladorUrlEnum {

	URL4001("PORCENTAJESFSPADIURLENUM4001", "4001");

	private final String key;
	private final String value;

	private PorcentajesFspAdiControladorUrlEnum(String key, String value) {
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