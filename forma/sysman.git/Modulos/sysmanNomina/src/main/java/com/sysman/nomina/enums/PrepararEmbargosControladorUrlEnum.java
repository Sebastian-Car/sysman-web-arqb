/*
 * PrepararEmbargosControladorUrlEnum
 *
 * 1.0
 *
 * 19/10/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum PrepararEmbargosControladorUrlEnum {

    URL28520("PREPARAREMBARGOSCONTROLADORURL28520", "7024"),

    URL28521("PREPARAREMBARGOSCONTROLADORURL28521", "471006"),

    URL28522("PREPARAREMBARGOSCONTROLADORURL28522", "471003");

    private final String key;
    private final String value;

    private PrepararEmbargosControladorUrlEnum(String key, String value)
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
