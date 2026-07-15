package com.sysman.plandesarrollo.enums;

public enum FrmTableroControlControladorEnum {
	
	INFORME("800376TableroDeControl");
	
	 private final String value;

    private FrmTableroControlControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}