package com.sysman.contabilidad.enums;

public enum FrmConceptosoServiciosEnum {
	
	ANOPREPARAR("ANOPREPARAR");

    private final String value;

    private FrmConceptosoServiciosEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
