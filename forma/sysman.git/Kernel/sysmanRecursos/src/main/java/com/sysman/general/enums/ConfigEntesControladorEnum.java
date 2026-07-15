package com.sysman.general.enums;

public enum ConfigEntesControladorEnum {
		CATEGORIA("21"),
		PARAM_CATEGORIA("CATEGORIA");
	
	private final String value;
	
	private  ConfigEntesControladorEnum(String value) {
	this.value = value;
	}
	
	public String getValue() {
	return value;
	}
}
