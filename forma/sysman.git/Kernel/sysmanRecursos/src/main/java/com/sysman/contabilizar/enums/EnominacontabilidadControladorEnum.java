package com.sysman.contabilizar.enums;

public enum EnominacontabilidadControladorEnum {

    PROCESO("PROCESO"),
    CODIGO("CODIGO"),
    NOMBRE("NOMBRE"),
    IDPROCESO("ID_DE_PROCESO"),
    MENSAJE("TB_TB874");

    private final String value;

    private EnominacontabilidadControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
