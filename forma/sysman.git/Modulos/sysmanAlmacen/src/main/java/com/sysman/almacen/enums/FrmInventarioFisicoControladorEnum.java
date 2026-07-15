package com.sysman.almacen.enums;

public enum FrmInventarioFisicoControladorEnum {

	BODEGA("BODEGA");

    private final String value;

    private FrmInventarioFisicoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}