/*
 * FrminfincautadosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrminfincautadosControladorUrlEnum {

    URL3613("FRMINFAUTOAVALUOSCONTROLADORURL3613", "367082"),

    URL4743("FRMINFAUTOAVALUOSCONTROLADORURL4743", "367084");

    private final String key;
    private final String value;

    private FrminfincautadosControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
