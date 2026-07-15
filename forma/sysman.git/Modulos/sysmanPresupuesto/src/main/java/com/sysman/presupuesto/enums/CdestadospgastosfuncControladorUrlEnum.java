/*
 * CdestadospgastosfuncControladorUrlEnum
 *
 * 1.0
 *
 * 28/11/2017
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
public enum CdestadospgastosfuncControladorUrlEnum {

    URL2580("CdestadospgastosfuncControladorURL2580", "4001"),

    URL2581("CdestadospgastosfuncControladorURL2581", "45018"),

    URL2582("CdestadospgastosfuncControladorURL2582", "45020");

    private final String key;
    private final String value;

    private CdestadospgastosfuncControladorUrlEnum(String key, String value)
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
