package com.sysman.workflow.enums;

public enum FrmReporteProcedenciaControladorUrlEnum {

	URL988010("FRMREPORTEPROCEDENCIACONTROLADORURL","988010"),

	URL988012("FRMREPORTEPROCEDENCIACONTROLADORURL","988012"),

	URL1040008("FRMREPORTEPROCEDENCIACONTROLADORURL","1040008"),

	URL1040010("FRMREPORTEPROCEDENCIACONTROLADORURL","1040010"),
	
	;

	private final String key;
	private final String value;

	private FrmReporteProcedenciaControladorUrlEnum(String key, String value) {
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
