/*
 * PeriodosControladorUrlEnum
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
public enum PeriodosControladorUrlEnum {

    URL4964("PERIODOSCONTROLADORURL4964", "7023"),

    URL4925("PERIODOSCONTROLADORURL4925", "4028"),

    URL4927("PERIODOSCONTROLADORURL4925", "27049"),

    URL4929("PERIODOSCONTROLADORURL4929", "4049"),

    URL4931("PERIODOSCONTROLADORURL4931", "537009");
    private final String key;
    private final String value;

    private PeriodosControladorUrlEnum(String key, String value)
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
