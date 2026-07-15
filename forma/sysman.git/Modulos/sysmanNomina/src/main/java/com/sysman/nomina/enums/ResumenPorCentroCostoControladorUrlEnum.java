/*
 * ResumenPorCentroCostoControladorUrlEnum
 *
 * 1.0
 *
 * 26/10/2017
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
public enum ResumenPorCentroCostoControladorUrlEnum {

    URL4750("RESUMENPORCENTROCOSTOCONTROLADORURL4750", "537002"),

    URL4751("RESUMENPORCENTROCOSTOCONTROLADORURL4751", "471002"),

    URL4752("RESUMENPORCENTROCOSTOCONTROLADORURL4752", "7024"),

    URL4753("RESUMENPORCENTROCOSTOCONTROLADORURL4753", "471003"),

    URL4754("RESUMENPORCENTROCOSTOCONTROLADORURL4754", "20056")

    ;

    private final String key;
    private final String value;

    private ResumenPorCentroCostoControladorUrlEnum(String key, String value)
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
