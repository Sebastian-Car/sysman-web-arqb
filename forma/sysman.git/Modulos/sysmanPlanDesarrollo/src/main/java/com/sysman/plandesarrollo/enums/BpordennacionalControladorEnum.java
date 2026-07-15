package com.sysman.plandesarrollo.enums;

public enum BpordennacionalControladorEnum {

	COD_PLAN("COD_PLAN"),
	COD_ACUERDO("COD_ACUERDO"),
	ACUERDO("ACUERDO"),
	COD_PILAR("COD_PILAR"),
	PILAR("PILAR"),
	INICIATIVA("INICIATIVA");

    private final String value;

    private BpordennacionalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
