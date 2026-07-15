package com.sysman.nomina.enums;

public enum FrmDetalleLiquidacionControladorUrlEnum {

	URL210169("REPORTEACUMULADOSCONTROLADORURL9007","210169");

	private final String key;
	private final String value;

	private  FrmDetalleLiquidacionControladorUrlEnum(String key, String value) {
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
