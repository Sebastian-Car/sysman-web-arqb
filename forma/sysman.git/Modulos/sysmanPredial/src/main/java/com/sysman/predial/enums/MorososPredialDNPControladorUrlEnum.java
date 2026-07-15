/*
 * MorososPredialDNPControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MorososPredialDNPControladorUrlEnum {

    URL8370("MOROSOSPREDIALDNPCONTROLADORURL8370", "381001"),

    URL8745("MOROSOSPREDIALDNPCONTROLADORURL8745", "381002");

    private final String key;
    private final String value;

    private MorososPredialDNPControladorUrlEnum(String key, String value)
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
