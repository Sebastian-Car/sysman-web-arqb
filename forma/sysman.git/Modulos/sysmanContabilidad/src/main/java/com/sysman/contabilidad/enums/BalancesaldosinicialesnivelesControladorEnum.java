package com.sysman.contabilidad.enums;

public enum BalancesaldosinicialesnivelesControladorEnum {

	COMPANIA("COMPANIA");

	private final String value;

	private BalancesaldosinicialesnivelesControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
