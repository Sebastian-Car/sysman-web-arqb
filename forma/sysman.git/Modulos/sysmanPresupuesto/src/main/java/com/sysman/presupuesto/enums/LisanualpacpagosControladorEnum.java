package com.sysman.presupuesto.enums;

public enum LisanualpacpagosControladorEnum {

    CODIGO("CODIGO"),

    PARAM1("NATURALEZA"),

    CUENTAINICIAL("CUENTAINICIAL");

    private final String value;

    private LisanualpacpagosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
