/*
 * SubeNovedadesControladorUrlEnum
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
public enum SubeNovedadesControladorUrlEnum {

    URL8948("SUBENOVEDADESCONTROLADORURL8948", "151048"),

    URL6298("SUBENOVEDADESCONTROLADORURL6298", "7027"),

    URL5617("SUBENOVEDADESCONTROLADORURL5617", "471008"),

    URL7130("SUBENOVEDADESCONTROLADORURL7130", "471048"),

    URL8216("SUBENOVEDADESCONTROLADORURL8216", "658001");

    private final String key;
    private final String value;

    private SubeNovedadesControladorUrlEnum(String key, String value)
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
