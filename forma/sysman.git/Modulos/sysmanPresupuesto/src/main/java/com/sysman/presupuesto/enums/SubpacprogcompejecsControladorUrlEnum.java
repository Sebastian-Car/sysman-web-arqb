/*
 * SubpacprogcompejecsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum SubpacprogcompejecsControladorUrlEnum {

    URL8533("SUBPACPROGCOMPEJECSCONTROLADORURL8533", "133001"),
    URL8535("SUBPACPROGCOMPEJECSCONTROLADORURL8535", "132001"),
    URL8534("SUBPACPROGCOMPEJECSCONTROLADORURL8534", "134002");

    private final String key;
    private final String value;

    private SubpacprogcompejecsControladorUrlEnum(String key, String value)
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
