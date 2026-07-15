package com.sysman.almacen.enums;

public enum FrmDepreciacionAcumuladaControladorUrlEnum {

	URL112044("FRMDEPRECIACIONACUMULADACONTROLADORURL01","112196"),
	
	URL141158("FRMDEPRECIACIONACUMULADACONTROLADORURL02","141158"),
	
	URL179003("FRMDEPRECIACIONACUMULADACONTROLADORURL03","179003"),
	
	URL179004("FRMDEPRECIACIONACUMULADACONTROLADORURL04","179004"),
	
	URL179008("FRMDEPRECIACIONACUMULADACONTROLADORURL03","179008");
	
	private final String key;
	private final String value;
	
	private  FrmDepreciacionAcumuladaControladorUrlEnum(String key, String value) {
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