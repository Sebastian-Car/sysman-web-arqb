/*
 * PredialreldiacaringrereanoControladorUrlEnum
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
public enum PredialreldiacaringrereanoControladorUrlEnum {

    URL7567("PREDIALRELDIACARINGREREANOCONTROLADORURL7567",
                    "375006"), URL5490(
                                    "PREDIALRELDIACARINGREREANOCONTROLADORURL5490",
                                    "408004"), URL4457(
                                                    "PREDIALRELDIACARINGREREANOCONTROLADORURL4457",
                                                    "408002"), URL7039("PREDIALRELDIACARINGREREANOCONTROLADORURL7039",
                                                                    "375004"), URL7029("PREDIALRELDIACARINGREREANOCONTROLADORURL7029",
                                                                                    "381004");
    private final String key;
    private final String value;

    private PredialreldiacaringrereanoControladorUrlEnum(String key, String value)
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
