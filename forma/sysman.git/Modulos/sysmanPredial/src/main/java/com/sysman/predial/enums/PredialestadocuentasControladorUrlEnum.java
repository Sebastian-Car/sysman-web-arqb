/*
 * PredialestadocuentasControladorUrlEnum
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
public enum PredialestadocuentasControladorUrlEnum {

    URL4045("PREDIALESTADOCUENTASCONTROLADORURL4045", "367013"),

    URL5227("PREDIALESTADOCUENTASCONTROLADORURL5227", "367171");

    private final String key;
    private final String value;

    private PredialestadocuentasControladorUrlEnum(String key, String value)
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
