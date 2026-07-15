package com.sysman.almacen.enums;

public enum InmueblesisControladorUrlEnum {
	
    URL001("INMUEBLESISCONTROLADORURLENUM001","112150"),
    
    URL002("INMUEBLESISCONTROLADORURLENUM002","141137"); 
	
	private final String key;
	private final String value;
	
	private  InmueblesisControladorUrlEnum(String key, String value) {
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
