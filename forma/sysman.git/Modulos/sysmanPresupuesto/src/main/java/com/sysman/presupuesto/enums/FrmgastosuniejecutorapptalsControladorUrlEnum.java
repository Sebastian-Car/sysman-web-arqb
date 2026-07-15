package com.sysman.presupuesto.enums;

public enum FrmgastosuniejecutorapptalsControladorUrlEnum {
	URL4001("FRMGASTOSUNIEJECUTORAPPTALSCONTROLADORURL4001", "4001"),
	URL7007("FRMGASTOSUNIEJECUTORAPPTALSCONTROLADORURL7007", "7007"),
	URL1992001("FRMGASTOSUNIEJECUTORAPPTALSCONTROLADORURL1992001", "1992001"),
	URL45117("FRMGASTOSUNIEJECUTORAPPTALSCONTROLADORURL45117", "45117"),
    URL45119("FRMGASTOSUNIEJECUTORAPPTALSCONTROLADORURL45119", "45119");
	
	private final String key;
	private final String value;
    private FrmgastosuniejecutorapptalsControladorUrlEnum(String key, String value) {
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
