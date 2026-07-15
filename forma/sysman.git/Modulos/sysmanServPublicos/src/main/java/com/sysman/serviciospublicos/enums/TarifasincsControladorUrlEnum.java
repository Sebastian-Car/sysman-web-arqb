/*
 * SaldocreditosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author jguerrero
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum TarifasincsControladorUrlEnum {

    URL0001("TARIFASINCSCONTROLADORURL0001",
                    "214012"),

    URL0002("TARIFASINCSCONTROLADORURL0002",
                    "227012"),

    URL0003("TARIFASINCSCONTROLADORURL0003",
                    "229004"),

    URL0004("TARIFASINCSCONTROLADORURL0004",
                    "309013");

    private final String key;
    private final String value;

    private TarifasincsControladorUrlEnum(String key, String value)
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
