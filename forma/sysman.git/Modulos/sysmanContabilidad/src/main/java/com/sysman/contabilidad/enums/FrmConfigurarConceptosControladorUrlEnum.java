package com.sysman.contabilidad.enums;

public enum FrmConfigurarConceptosControladorUrlEnum {

	URL4366("FRMCONFIGURARFLUJOEFECTIVOCONTROLADORURL4366", "4001"),
	
	URL1988001("FRMCONFIGURARFLUJOEFECTIVOCONTROLADORURL4366", "1988001"),
	
	URL4072("FRMCONFIGURARFLUJOEFECTIVOCONTROLADORURL4072", "4072"),
	
	;

	private final String key;
	private final String value;

	private FrmConfigurarConceptosControladorUrlEnum(String key,
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

