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
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SaldocreditosControladorUrlEnum {

    URL0001("SALDOCREDITOSCONTROLADORURL0001", "251007"),

    URL5229("SALDOCREDITOSCONTROLADORURL5229", "213188"),

    URL4950("SALDOCREDITOSCONTROLADORURL4950", "214005");

    private final String key;
    private final String value;

    private SaldocreditosControladorUrlEnum(String key, String value) {
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
