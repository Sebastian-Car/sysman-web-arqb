/*
 * LiscomppptalesControladorUrlEnum
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
public enum LisejecpptalgastosControladorUrlEnum {

    URL4589("LISEJECPPTALGASTOSCONTROLADORURL4589", "94030"),

    URL5292("LISEJECPPTALGASTOSCONTROLADORURL5292", "94032"),

    URL6866("LISEJECPPTALGASTOSCONTROLADORURL6866", "4001"),

    URL6867("LISEJECPPTALGASTOSCONTROLADORURL6867", "62007"),

    URL6868("LISEJECPPTALGASTOSCONTROLADORURL6868", "62011");

    private final String key;
    private final String value;

    private LisejecpptalgastosControladorUrlEnum(String key, String value)
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