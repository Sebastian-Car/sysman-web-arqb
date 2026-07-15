/*
 * FinaneducacionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FinaneducacionsControladorUrlEnum {

    URL4120("FINANEDUCACIONSCONTROLADORURL4120", "704002"),

    URL7878("FINANEDUCACIONSCONTROLADORURL7878", "639003"),

    URL9591("FINANEDUCACIONSCONTROLADORURL9591", "706005"),

    URL6565("FINANEDUCACIONSCONTROLADORURL6565", "70600D"),

    URL5651("FINANEDUCACIONSCONTROLADORURL5651", "706004"),

    URL3535("FINANEDUCACIONSCONTROLADORURL3535", "706007");

    private final String key;
    private final String value;

    private FinaneducacionsControladorUrlEnum(String key, String value) {
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
