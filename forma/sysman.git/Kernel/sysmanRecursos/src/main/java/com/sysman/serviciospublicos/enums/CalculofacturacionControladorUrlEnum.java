/*
 * CalculofacturacionControladorUrlEnum
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
public enum CalculofacturacionControladorUrlEnum {

    URL6464("CALCULOFACTURACIONCONTROLADORURL6464", "214003"),
    URL6565("CALCULOFACTURACIONCONTROLADORURL6565", "318001")
    ;

    private final String key;
    private final String value;

    private CalculofacturacionControladorUrlEnum(String key, String value)
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
