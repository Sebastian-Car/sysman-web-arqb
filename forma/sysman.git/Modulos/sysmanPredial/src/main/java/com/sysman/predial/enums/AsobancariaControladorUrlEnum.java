/*
 * AsobancariaControladorUrlEnum
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
public enum AsobancariaControladorUrlEnum {

    URL5588("ASOBANCARIACONTROLADORURL5588", "375001"),

    URL5991("ASOBANCARIACONTROLADORURL5991", "375002");

    private final String key;
    private final String value;

    private AsobancariaControladorUrlEnum(String key, String value) {
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
