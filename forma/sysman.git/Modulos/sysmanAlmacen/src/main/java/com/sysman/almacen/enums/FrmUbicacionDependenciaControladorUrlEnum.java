package com.sysman.almacen.enums;

public enum FrmUbicacionDependenciaControladorUrlEnum {
	
	URL1922001("1922001","1922001"),
	URL6115("6115","62002");

	private final String key;
	private final String value;
	
	private  FrmUbicacionDependenciaControladorUrlEnum(String key, String value) {
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
