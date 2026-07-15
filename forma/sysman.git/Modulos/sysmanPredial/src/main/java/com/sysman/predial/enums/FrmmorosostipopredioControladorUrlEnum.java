/*
 * FrmmorosostipopredioControladorUrlEnum
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
public enum FrmmorosostipopredioControladorUrlEnum {

    URL4409("FRMMOROSOSTIPOPREDIOCONTROLADORURL4409", "379003"),

    URL5371("FRMMOROSOSTIPOPREDIOCONTROLADORURL5371", "379005");

    private final String key;
    private final String value;

    private FrmmorosostipopredioControladorUrlEnum(String key, String value)
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
