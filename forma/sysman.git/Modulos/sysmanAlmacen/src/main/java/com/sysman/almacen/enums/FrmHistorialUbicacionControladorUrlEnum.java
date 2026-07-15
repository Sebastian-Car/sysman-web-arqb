package com.sysman.almacen.enums;

public enum FrmHistorialUbicacionControladorUrlEnum {
	
	URL259200("FRMHISTORIALUBICACIONCONTROLADORURL01", "112203"), 

	URL259201("FRMHISTORIALUBICACIONCONTROLADORURL02", "141165");

	private final String key;
	private final String value;

	private FrmHistorialUbicacionControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}