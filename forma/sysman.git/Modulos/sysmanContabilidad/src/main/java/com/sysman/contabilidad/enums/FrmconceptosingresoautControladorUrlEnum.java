package com.sysman.contabilidad.enums;

public enum FrmconceptosingresoautControladorUrlEnum {
	
	URL4001("FRMCONCEPTOSINGRESOAUTCONTROLADORURL", "4001"),
	
	URL4016("FRMCONCEPTOSINGRESOAUTCONTROLADORURL", "4016"),
	
	URL16221("FRMCONCEPTOSINGRESOAUTCONTROLADORURL", "16221"),
	
	URL1997001("FRMCONCEPTOSINGRESOAUTCONTROLADORURL", "1997001"),
	
	URL1997002("FRMCONCEPTOSINGRESOAUTCONTROLADORURL", "1997002");

    private final String key;
    private final String value;

    private FrmconceptosingresoautControladorUrlEnum(String key, String value) {
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
