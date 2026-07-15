package com.sysman.general.enums;

public enum FrmReporteAuditoriaControladorUrlEnum {

	URL1991003("FRMREPORTEAUDITORIACONTROLADORURLENUM", "1991003"),
	
	URL1991005("FRMREPORTEAUDITORIACONTROLADORURLENUM", "1991005");

	private final String key;
	private final String value;

	private FrmReporteAuditoriaControladorUrlEnum(String key,
			String value) {
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
