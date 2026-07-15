package com.sysman.contabilidad.enums;

public enum InformeDeRetencionesControladorEnum {

	NITINICIAL("NITINICIAL");

	private final String value;

	private InformeDeRetencionesControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
