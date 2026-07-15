/*
 * FrmponderacionsControladorUrlEnum
 *
 * 1.0
 *
 * 21/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmponderacionsControladorUrlEnum {

    URL4310("FRMPONDERACIONSCONTROLADORURL4310", "552007"),

    URL4311("FRMPONDERACIONSCONTROLADORURL4311", "4001"),

    URL4312("FRMPONDERACIONSCONTROLADORURL4312", "552009"),

    URL4313("FRMPONDERACIONSCONTROLADORURL4313", "552010");

    private final String key;
    private final String value;

    private FrmponderacionsControladorUrlEnum(String key,
        String value)
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
