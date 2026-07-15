package com.sysman.nomina.enums;

public enum FrmListaEncargosControladorUrlEnum {
	
	 URL0001("FRMLISTAENCARGOSCONTROLADORURL001", "613004"),
	 
	 URL0002("FRMLISTAENCARGOSCONTROLADORURL002", "613006");
	
    private final String key;
    private final String value;

    private FrmListaEncargosControladorUrlEnum(String key,
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
