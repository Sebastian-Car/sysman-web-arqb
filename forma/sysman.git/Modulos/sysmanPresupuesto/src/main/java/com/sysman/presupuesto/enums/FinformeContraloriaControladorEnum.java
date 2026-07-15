package com.sysman.presupuesto.enums;

public enum FinformeContraloriaControladorEnum {
	
	CUENTAINICIAL("CUENTAINICIAL"); /*  */

    private final String value;

    private FinformeContraloriaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
