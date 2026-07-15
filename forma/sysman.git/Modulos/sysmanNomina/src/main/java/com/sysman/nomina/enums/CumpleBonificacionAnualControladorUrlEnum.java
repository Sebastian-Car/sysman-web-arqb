/*
 * CumpleBonificacionAnualControladorUrlEnum
 *
 * 1.0
 *
 * 05/09/2017
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
public enum CumpleBonificacionAnualControladorUrlEnum {

    URL3100("CUMPLEBONIFICACIONANUALCONTROLADORURL3100", "7025");

    private final String key;
    private final String value;

    private CumpleBonificacionAnualControladorUrlEnum(String key, String value)
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
