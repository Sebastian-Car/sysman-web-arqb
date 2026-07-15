package com.sysman.contabilidad.enums;


public enum  frcuentaporpagarespControladorUrlEnum {
	
	URL29025("FRCUENTAPORPAGARESPCONTROLADORURL29025", "29025"),
	
    URL2759("FRCUENTAPORPAGARESPCONTROLADORURL14036", "14036"),

    URL3332("FRCUENTAPORPAGARESPCONTROLADORURL14033", "14033"),

	URL4002("FRCUENTAPORPAGARESPCONTROLADORURL4002", "4002");

	private final String key;
	private final String value;

	private frcuentaporpagarespControladorUrlEnum(String key, String value) {
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
