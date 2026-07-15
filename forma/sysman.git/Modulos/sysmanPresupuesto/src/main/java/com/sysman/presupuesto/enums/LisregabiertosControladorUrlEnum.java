/*
 * LisauxpptalControladorUrlEnum
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
public enum LisregabiertosControladorUrlEnum {

    URL5636("LISREGABIERTOSCONTROLADORURL5636", "13028"), URL5746(
                    "LISREGABIERTOSCONTROLADORURL5746", "13028");

    private final String key;
    private final String value;

    private LisregabiertosControladorUrlEnum(String key, String value)
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
