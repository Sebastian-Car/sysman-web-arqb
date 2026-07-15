/*
 * FrmfechasuspensionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmfechasuspensionsControladorUrlEnum {

    URL3973("FRMFECHASUSPENSIONSCONTROLADORURL3973", "213092"),

    URL3974("FRMFECHASUSPENSIONSCONTROLADORURL3974", "213094"),

    URL4740("FRMFECHASUSPENSIONSCONTROLADORURL4740", "214052");

    private final String key;
    private final String value;

    private FrmfechasuspensionsControladorUrlEnum(String key, String value)
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
