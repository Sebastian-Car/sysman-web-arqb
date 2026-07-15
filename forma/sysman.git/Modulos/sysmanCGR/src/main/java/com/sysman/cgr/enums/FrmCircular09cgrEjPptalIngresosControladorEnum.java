package com.sysman.cgr.enums;

public enum FrmCircular09cgrEjPptalIngresosControladorEnum {
	

    PARAM1("NATURALEZA"),
    
    PARAM2("CUENTAINICIAL");

    private final String value;

    private FrmCircular09cgrEjPptalIngresosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
