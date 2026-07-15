package com.sysman.nomina.enums;

public enum CambioCajaCompensacionControladorUrlEnum {

	URL644004("CAMBIOCAJACOMPENSACIONCONTROLADOR644004", "644004"),

	URL210161("CAMBIOCAJACOMPENSACIONCONTROLADOR2010161", "210161"),

	URL210162("CAMBIOCAJACOMPENSACIONCONTROLADOR2010162", "210162"),
	
	URL210012("CAMBIOCAJACOMPENSACIONCONTROLADOR2010162", "210163");

	private final String key;
	private final String value;

	private CambioCajaCompensacionControladorUrlEnum(String key, String value) {
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
