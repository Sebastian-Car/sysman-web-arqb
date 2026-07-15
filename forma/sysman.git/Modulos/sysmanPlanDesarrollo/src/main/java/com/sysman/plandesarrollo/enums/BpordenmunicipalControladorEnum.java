package com.sysman.plandesarrollo.enums;

public enum BpordenmunicipalControladorEnum {
	
	COD_PLAN("COD_PLAN"),
	POLITICA("POLITICA"),
	CATEGORIA("CATEGORIA"),
	COD_POLITICA("COD_POLITICA"),
	TIPOP("TIPOP"),
	COD_CATEGORIA("COD_CATEGORIA"),
	TIPOC("TIPOC"),
	COD_SUBCATEGORIA("COD_SUBCATEGORIA"),
	TIPOSUB("TIPOSUB");

    private final String value;

    private BpordenmunicipalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
