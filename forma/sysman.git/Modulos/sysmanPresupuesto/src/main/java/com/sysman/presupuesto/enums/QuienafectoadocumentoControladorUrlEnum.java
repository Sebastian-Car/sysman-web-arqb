/*
 * QuienafectoadocumentoControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum QuienafectoadocumentoControladorUrlEnum {

    URL2972("QUIENAFECTOADOCUMENTOCONTROLADORURL2972",
                    "25008"),

    URL5959("QUIENAFECTOADOCUMENTOCONTROLADORURL5959",
                    "38049"),
	
	URL5960("QUIENAFECTOADOCUMENTOCONTROLADORURL5960", "4001");

    private final String key;
    private final String value;

    private QuienafectoadocumentoControladorUrlEnum(String key, String value)
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
