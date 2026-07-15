package com.sysman.general.enums;

public enum FrmCarterafinanciablesControladorEnum {

	MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

	PARAM0("CUENTAINI"), 

	PARAM1("NIT");

	private final String value;
	private FrmCarterafinanciablesControladorEnum(String a) {
		this.value=a;
	}
	public String getValue() {
		return value;
	}
}
