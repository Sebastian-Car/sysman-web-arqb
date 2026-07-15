/*
 * ListadoResIGACControladorUrlEnum
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
public enum ListadoResIGACControladorUrlEnum {

    URL5808("LISTADORESIGACCONTROLADORURL5808", "406001"),

    URL5240("LISTADORESIGACCONTROLADORURL5240", "406003"),

    URL5241("LISTADORESIGACCONTROLADORURL5241", "406003"),

    URL5242("LISTADORESIGACCONTROLADORURL5242", "367103"),

    URL5243("LISTADORESIGACCONTROLADORURL5243", "367105");

    private final String key;
    private final String value;

    private ListadoResIGACControladorUrlEnum(String key, String value)
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
