package com.sysman.contabilidad.enums;

public enum FrmenviosigecControladorUrlEnum {

	URL001("FRMENVIOSIGECCONTROLADORURLENUM3578", "1928006"),
	URL002("FRMENVIOSIGECCONTROLADORURLENUM1895016", "1928007"),
	URL003("FRMENVIOSIGECCONTROLADORURLENUM1895016", "1928008");

	private final String key;
	private final String value;

	private FrmenviosigecControladorUrlEnum(String key, String value) {
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
