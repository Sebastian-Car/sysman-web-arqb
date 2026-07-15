/*
 * LibrodebancosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum LibrodebancosControladorUrlEnum {

    URL4055("LIBRODEBANCOSCONTROLADORURL4055", "15003"),

    URL3361("LIBRODEBANCOSCONTROLADORURL3361", "15005"),

    URL4888("LIBRODEBANCOSCONTROLADORURL4888", "29118"),

    URL6232("LIBRODEBANCOSCONTROLADORURL6232", "29120"),

    URL6234("LIBRODEBANCOSCONTROLADORURL6234", "13003"),

    URL6235("LIBRODEBANCOSCONTROLADORURL6234", "13005");

    private final String key;
    private final String value;

    private LibrodebancosControladorUrlEnum(String key, String value)
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
