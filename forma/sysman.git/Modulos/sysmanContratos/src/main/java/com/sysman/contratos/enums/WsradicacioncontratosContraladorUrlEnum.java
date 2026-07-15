/*
 * WsradicacioncontratosContraladorUrlEnum
 *
 * 1.0
 *
 * 15/08/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum WsradicacioncontratosContraladorUrlEnum {

    URL3320("WSRADICACIONCONTRATOSCONTRALADORURL3320", "73016"),

    URL3321("WSRADICACIONCONTRATOSCONTRALADORURL3321", "82091"),

    URL3322("WSRADICACIONCONTRATOSCONTRALADORURL3322", "14095")

    ;

    private final String key;
    private final String value;

    private WsradicacioncontratosContraladorUrlEnum(String key, String value)
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
