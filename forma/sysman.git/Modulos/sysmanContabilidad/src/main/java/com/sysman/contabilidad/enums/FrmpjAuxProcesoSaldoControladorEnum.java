package com.sysman.contabilidad.enums;

public enum FrmpjAuxProcesoSaldoControladorEnum {

	PARAM0("CUENTAINICIAL"),

	PARAM1("TECEROINICIAL"),

	PARAM2("NIT"), 

	PARAM3("CODIGOINICIAL"),

	PARAM4("NUMEROPROCESO");

	private final String value;

	private FrmpjAuxProcesoSaldoControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
