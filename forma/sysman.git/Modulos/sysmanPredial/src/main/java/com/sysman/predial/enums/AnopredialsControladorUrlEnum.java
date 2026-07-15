/*
 * AnopredialsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AnopredialsControladorUrlEnum {

    URL3664("ANOPREDIALSCONTROLADORURL3664", "4001"),

    URL0001("ANOPREDIALSCONTROLADORURL0001", "4020"),

    URL0002("ANOPREDIALSCONTROLADORURL0002", "4021"),

    URL0003("ANOPREDIALSCONTROLADORURL0003", "4023"),

    URL0004("ANOPREDIALSCONTROLADORURL0004", "34022");

    private final String key;
    private final String value;

    private AnopredialsControladorUrlEnum(String key, String value) {
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
