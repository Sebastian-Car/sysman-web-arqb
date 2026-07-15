package com.sysman.nomina.enums;

public enum FrmActDatosReformaControladorUrlEnum {

	URL640001("FRMACTDATOSREFORMA640001", "640001"),
	
	URL210167("FRMACTDATOSREFORMA210167", "210167");

	private final String key;
	private final String value;

	private FrmActDatosReformaControladorUrlEnum(String key, String value) {
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
