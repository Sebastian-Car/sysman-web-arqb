package com.sysman.facturaciongeneral.enums;

public enum FrmDevolucionFacturaControladorUrlEnum {

	URL3595("FRMDEVOLUCIONFACTURACONTROLADORURLENUM3595", "666017"),
	
	URL16230("FRMDEVOLUCIONFACTURACONTROLADORURLENUM3595", "16230");

	private final String key;
	private final String value;

	private FrmDevolucionFacturaControladorUrlEnum(String key, String value) {
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
