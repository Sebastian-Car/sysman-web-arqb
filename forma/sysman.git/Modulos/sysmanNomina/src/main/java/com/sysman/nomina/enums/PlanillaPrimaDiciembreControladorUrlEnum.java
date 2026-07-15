/*
 * PlanillaPrimaDiciembreControladorUrlEnum
 *
 * 1.0
 *
 * 19/10/2017
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
public enum PlanillaPrimaDiciembreControladorUrlEnum {

    URL28520("PLANILLAPRIMADICIEMBRECONTROLADORURL28520", "471002"),

    URL28521("PLANILLAPRIMADICIEMBRECONTROLADORURL28521", "7024"),

    URL28522("PLANILLAPRIMADICIEMBRECONTROLADORURL28522", "471003");

    private final String key;
    private final String value;

    private PlanillaPrimaDiciembreControladorUrlEnum(String key, String value)
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
