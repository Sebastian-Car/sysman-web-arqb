/*
 * ResumPorCentroCostoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ResumPorCentroCostoControladorUrlEnum {

    URL7104("RESUMPORCENTROCOSTOCONTROLADORURL7104", "7024"),

    URL6594("RESUMPORCENTROCOSTOCONTROLADORURL6594", "471003"),

    URL10526("RESUMPORCENTROCOSTOCONTROLADORURL10526", "537002"),

    URL6086("RESUMPORCENTROCOSTOCONTROLADORURL6086", "471002"),

    URL11399("RESUMPORCENTROCOSTOCONTROLADORURL11399", "20040");

    private final String key;
    private final String value;

    private ResumPorCentroCostoControladorUrlEnum(String key, String value)
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
