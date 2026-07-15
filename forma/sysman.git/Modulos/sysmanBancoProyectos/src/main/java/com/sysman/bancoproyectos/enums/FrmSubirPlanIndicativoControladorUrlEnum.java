package com.sysman.bancoproyectos.enums;

public enum FrmSubirPlanIndicativoControladorUrlEnum {

	URL4001("FRMSUBIRPLANINDICATIVOCONTROLADORURL4001", "4001"),
	
	URL553006("FRMSUBIRPLANINDICATIVOCONTROLADORURL553006", "553006"),
	
	URL203009("FRMSUBIRPLANINDICATIVOCONTROLADORURL203009", "203009"),
	
	URL62112("FRMSUBIRPLANINDICATIVOCONTROLADORURL62112", "62112"),

	URL554026("FRMSUBIRPLANINDICATIVOCONTROLADORURL554026", "554026");

	private final String key;
	private final String value;

	private FrmSubirPlanIndicativoControladorUrlEnum(String key, String value) {
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