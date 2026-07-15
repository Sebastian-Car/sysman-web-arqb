/*
 * AsobancariaexpControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AsobancariaexpControladorUrlEnum {

    URL6652("ASOBANCARIAEXPCONTROLADORURL6652", "214005"),

    URL6653("ASOBANCARIAEXPCONTROLADORURL6652", "213020"),

    URL6654("ASOBANCARIAEXPCONTROLADORURL6652", "213022");

    private final String key;
    private final String value;

    private AsobancariaexpControladorUrlEnum(String key, String value) {
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
