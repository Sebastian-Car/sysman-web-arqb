package com.sysman.plandesarrollo.enums;

public enum BpordendepartamentalControladorEnum {

	COD_PLAN("COD_PLAN"),
	CODIGO_EJE("CODIGO_EJE"),
	EJE("EJE"),
	CODIGO_MEDIDA("CODIGO_MEDIDA"),
	DESCRIPCION_EJE("DESCRIPCION_EJE"),
	DESCRIPCION_MEDIDA("DESCRIPCION_MEDIDA");

    private final String value;

    private BpordendepartamentalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
