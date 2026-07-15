package com.sysman.contabilidad.enums;

public enum frcuentaporpagardetControladorUrlEnum {

	URL29025("FRCUENTAPORPAGARDETCONTROLADOR29025", "29025"),

	URL4002("FRCUENTAPORPAGARDETCONTROLADOR4002", "4002");

	private final String key;
	private final String value;

	private frcuentaporpagardetControladorUrlEnum(String key, String value) {
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
