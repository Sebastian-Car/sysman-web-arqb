package com.sysman.contabilidad.enums;


public enum FrmDeterioroDeCarteraEnum {

	PARAM2("PARAM2"),  
	PARAM1("PARAM1"),  
	PARAM0("PARAM0");

	private final String value;

	private  FrmDeterioroDeCarteraEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

