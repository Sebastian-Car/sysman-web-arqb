/*
 * FactoresliqfinalControladorUrlEnum
 *
 * 1.0
 *
 * 09/10/2017
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
public enum FactoresliqfinalControladorUrlEnum {

    // listaAno1
    URL28520("FACTORESLIQFINALCONTROLADORURL28520", "471002"),

    // listaMes1
    URL28521("FACTORESLIQFINALCONTROLADORURL28521", "471034"),

    // listaPeriodo1
    URL28522("FACTORESLIQFINALCONTROLADORURL28522", "471035"),

    // listaEmpleado - estado = 1
    URL28523("FACTORESLIQFINALCONTROLADORURL28523", "210034");

    private final String key;
    private final String value;

    private FactoresliqfinalControladorUrlEnum(String key, String value)
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
