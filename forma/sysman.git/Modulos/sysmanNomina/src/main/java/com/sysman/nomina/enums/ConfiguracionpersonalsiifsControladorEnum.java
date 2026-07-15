package com.sysman.nomina.enums;

public enum ConfiguracionpersonalsiifsControladorEnum {

	COMPANIA("COMPANIA"), ID_DE_EMPLEADO("ID_DE_EMPLEADO"), NUMERO_DCTO("NUMERO_DCTO"), TIPOCUENTA("TIPOCUENTA"), BANCO(
			"BANCO"), NOMBRE("NOMBRE"), CUENTA("CUENTA"), NOMBRE_MEDIO_PAGO_SIIF("NOMBRE_MEDIO_PAGO_SIIF");

	private final String value;

	private ConfiguracionpersonalsiifsControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
