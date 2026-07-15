/*
 * AnularcertcatasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author jguerrero
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum AnularcertcatasControladorUrlEnum {

    URL3962("ANULARCERTCATASCONTROLADORURL3962",
                    "367002"),

    URL4786("ANULARCERTCATASCONTROLADORURL4786",
                    "372001");

    private final String key;
    private final String value;

    private AnularcertcatasControladorUrlEnum(String key, String value)
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
