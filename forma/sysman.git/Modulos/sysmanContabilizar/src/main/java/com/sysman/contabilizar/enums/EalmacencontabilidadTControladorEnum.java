package com.sysman.contabilizar.enums;

public enum EalmacencontabilidadTControladorEnum {

	ULTIMONUMERO("ULTIMONUMERO");

    private final String value;

    private EalmacencontabilidadTControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}