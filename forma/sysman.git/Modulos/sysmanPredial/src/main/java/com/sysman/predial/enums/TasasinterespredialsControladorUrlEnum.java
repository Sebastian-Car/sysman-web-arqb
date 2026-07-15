/*
 * TasasinterespredialsControladorUrlEnum
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
public enum TasasinterespredialsControladorUrlEnum {

    URL2264("TASASINTERESPREDIALSCONTROLADORURL2264", "4026"),

    URL2265("TASASINTERESPREDIALSCONTROLADORURL2265", "7023");

    private final String key;
    private final String value;

    private TasasinterespredialsControladorUrlEnum(String key, String value)
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
