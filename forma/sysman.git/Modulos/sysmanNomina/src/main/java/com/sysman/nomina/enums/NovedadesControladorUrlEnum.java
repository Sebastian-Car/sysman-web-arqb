/*
 * NovedadesControladorUrlEnum
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
public enum NovedadesControladorUrlEnum {

    URL28529("NOVEDADESCONTROLADORURL28529", "538001"),

    URL15494("NOVEDADESCONTROLADORURL15494", "538003"),

    URL17692("NOVEDADESCONTROLADORURL17692", "151017"),

    URL17629("NOVEDADESCONTROLADORURL17629", "471002"),

    URL17630("NOVEDADESCONTROLADORURL17630", "210025");

    private final String key;
    private final String value;

    private NovedadesControladorUrlEnum(String key, String value)
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
