package com.sysman.almacen.enums;

public enum AprobSolicitudSuministroControladorUrlEnum {

	URL39812("APROBSOLICITUDSUMINISTROCONTROLADOR001", "109028"),
	
	URL10900R("APROBSOLICITUDSUMINISTROCONTROLADOR001", "10900R"),
	
	URL109030("APROBSOLICITUDSUMINISTROCONTROLADOR001", "109030"),
	
	URL110012("APROBSOLICITUDSUMINISTROCONTROLADOR001", "110012");

	private final String key;
	private final String value;

	private AprobSolicitudSuministroControladorUrlEnum(String key, String value) {
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
