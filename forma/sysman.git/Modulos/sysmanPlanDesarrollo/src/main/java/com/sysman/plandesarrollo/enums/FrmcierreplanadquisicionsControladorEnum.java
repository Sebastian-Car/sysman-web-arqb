package com.sysman.plandesarrollo.enums;

public enum FrmcierreplanadquisicionsControladorEnum {
	COMPANIA("COMPANIA"), NUMERO("NUMERO"), FECHA_CIERRE_ADQUISICIONES(
			"FECHA_CIERRE_ADQUISICIONES"), FECHA_CIERRE_ADQUI_EJEC("FECHA_CIERRE_ADQUI_EJEC");

	private final String value;

	private FrmcierreplanadquisicionsControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
