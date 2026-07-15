/*
 * FalertasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FalertasControladorUrlEnum {

    URL8883("FALERTASCONTROLADORURL8883", "58001"),

    URL5505("FALERTASCONTROLADORURL5505", "463003"),

    URL5605("FALERTASCONTROLADORURL5605", "62007"),

    URL9893("FALERTASCONTROLADORURL9893", "899001");

    private final String key;
    private final String value;

    private FalertasControladorUrlEnum(String key, String value) {
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
