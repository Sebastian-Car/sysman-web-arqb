package com.sysman.presupuesto.enums;

public enum FrmCargarPRMfuenteUrlEnum {

	URL0001("FRMDISRESDUCONTROLADORURL4371","45031"),
	
	URL0002("FRMDISRESDUCONTROLADORURL4371","45076");
	
	private final String key;
	private final String value;
	
	private  FrmCargarPRMfuenteUrlEnum(String key, String value) {
	    this.key   = key; 
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
