/*
 * DiscoDaviviendaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DiscoDaviviendaControladorUrlEnum {

    URL2889("DISCODAVIVIENDACONTROLADORURL2889", "471002"),

    URL5148("DISCODAVIVIENDACONTROLADORURL5148", "459001"),

    URL3496("DISCODAVIVIENDACONTROLADORURL3496", "471018"),

    URL3429("DISCODAVIVIENDACONTROLADORURL3429", "471019");

    private final String key;
    private final String value;

    private DiscoDaviviendaControladorUrlEnum(String key, String value)
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
