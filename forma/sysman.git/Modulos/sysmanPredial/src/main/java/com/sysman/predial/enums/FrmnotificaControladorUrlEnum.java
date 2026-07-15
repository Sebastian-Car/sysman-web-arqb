/*
 * FrmnotificaControladorUrlEnum
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
public enum FrmnotificaControladorUrlEnum {

    URL4409("FRMNOTIFICACONTROLADORURL4409", "402001"),

    URL5371("FRMNOTIFICACONTROLADORURL5371", "402002"),

    URL5373("FRMNOTIFICACONTROLADORURL5373", "402004"),

    URL5372("FRMNOTIFICACONTROLADORURL5372", "367096");

    private final String key;
    private final String value;

    private FrmnotificaControladorUrlEnum(String key, String value)
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
