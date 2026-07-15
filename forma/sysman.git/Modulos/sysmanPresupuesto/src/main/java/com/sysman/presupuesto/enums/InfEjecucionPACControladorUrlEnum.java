/*
 * InfEjecucionPACControladorUrlEnum
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
public enum InfEjecucionPACControladorUrlEnum {

    URL5780("INFEJECUCIONPACCONTROLADORURL5780", "45018"),

    URL8510("INFEJECUCIONPACCONTROLADORURL8510", "20011"),

    URL4539("INFEJECUCIONPACCONTROLADORURL4539", "7001"),

    URL4961("INFEJECUCIONPACCONTROLADORURL4961", "7004"),

    URL9229("INFEJECUCIONPACCONTROLADORURL9229", "23010"),

    URL7829("INFEJECUCIONPACCONTROLADORURL7829", "20013"),

    URL9811("INFEJECUCIONPACCONTROLADORURL9811", "23019"),

    URL5436("INFEJECUCIONPACCONTROLADORURL5436", "4001"),

    URL6756("INFEJECUCIONPACCONTROLADORURL6756", "45020");

    private final String key;
    private final String value;

    private InfEjecucionPACControladorUrlEnum(String key, String value)
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
