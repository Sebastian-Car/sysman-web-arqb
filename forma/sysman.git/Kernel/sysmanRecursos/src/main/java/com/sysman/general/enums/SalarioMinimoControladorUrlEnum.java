/*
 * SalarioMinimoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SalarioMinimoControladorUrlEnum {

    URL4731("SALARIOMINIMOCONTROLADORURL4731", "4006"),

    URL5567("SALARIOMINIMOCONTROLADORURL5567", "4005"),

    URL2170("SALARIOMINIMOCONTROLADORURL2170", "4003");

    private final String key;
    private final String value;

    private SalarioMinimoControladorUrlEnum(String key, String value)
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
