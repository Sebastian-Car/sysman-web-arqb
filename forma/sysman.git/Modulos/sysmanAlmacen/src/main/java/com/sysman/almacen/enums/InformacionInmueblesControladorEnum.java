package com.sysman.almacen.enums;

public enum InformacionInmueblesControladorEnum {
	
	CODIGO("CODIGO");
	
	private final String value;
	
	private  InformacionInmueblesControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
