package com.sysman.almacen.enums;

public enum ElementosBodegaPorContratoControladorUrlEnum {
	
	URL32003("ElementosBodegaPorContratoControladorUrl003",
			"32003"),
	
	URL32013("ElementosBodegaPorContratoControladorUrl013",
			"32013"),
	
	URL73056("ElementosBodegaPorContratoControladorUrl056",
			"73056");

	private final String key;
	private final String value;
	
	private ElementosBodegaPorContratoControladorUrlEnum(String key, String value) {
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
