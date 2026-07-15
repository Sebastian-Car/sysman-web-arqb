/*
 * LisejecucioningresospaControladorUrlEnum
 *
 * 1.0
 *
 * 06/12/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum LisejecucioningresospaControladorUrlEnum {

    URL9260("LISEJECUCIONINGRESOSPACONTROLADORURL9260", "45002"),

    URL9261("LISEJECUCIONINGRESOSPACONTROLADORURL9261", "45004"),

    URL9262("LISEJECUCIONINGRESOSPACONTROLADORURL9262", "4001");

    private final String key;
    private final String value;

    private LisejecucioningresospaControladorUrlEnum(String key, String value)
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
