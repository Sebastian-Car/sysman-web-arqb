package com.sysman.contabilidad.enums;

public enum FrmConfigurarPlanContableControladorUrlEnum {

	URL16234("FRMCONFIGURARPLANCONTABLECONTROLADORURL", "16234"),
	
	URL16236("FRMCONFIGURARPLANCONTABLECONTROLADORURL", "16236"),
	
	URL6990("FRMCONFIGURARPLANCONTABLECONTROLADORURL", "4001"),
	
	URL1985001("FRMCONFIGURARPLANCONTABLECONTROLADORURL", "1985001"),
	
	URL1985003("FRMCONFIGURARPLANCONTABLECONTROLADORURL", "1985003");

	private final String key;

	private final String value;

	private FrmConfigurarPlanContableControladorUrlEnum(String key, String value) {
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