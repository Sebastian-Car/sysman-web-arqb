package com.sysman.contabilidad.enums;

public enum ConceptosdocsoportedianControladorEnum {
	
	ANOPREPARAR("ANOPREPARAR");

    private final String value;

    private ConceptosdocsoportedianControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
