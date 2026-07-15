/*
 * CambiosPatrimonioChipControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfauxiliarsControladorControladorUrlEnum {

    URL2123("CONFAUXILIARSCONTROLADORCONTROLADORURL2123",
                    "23045"),

    URL2124("CONFAUXILIARSCONTROLADORCONTROLADORURL2124",
                    "23047"),

    URL2125("CONFAUXILIARSCONTROLADORCONTROLADORURL2125",
                    "4001"),

    URL2126("CONFAUXILIARSCONTROLADORCONTROLADORURL2126",
                    "4027"),

    URL2127("CONFAUXILIARSCONTROLADORCONTROLADORURL2127",
                    "1686003");

    private final String key;
    private final String value;

    private ConfauxiliarsControladorControladorUrlEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
