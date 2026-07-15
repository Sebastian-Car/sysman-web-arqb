/*
 * FactorescesantiasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FactorescesantiasControladorUrlEnum {

    URL6202("FACTORESCESANTIASCONTROLADORURL6202",
                    "471018"),

    URL8072("FACTORESCESANTIASCONTROLADORURL8072",
                    "210027"),

    URL5622("FACTORESCESANTIASCONTROLADORURL5622",
                    "471002"),

    URL7286("FACTORESCESANTIASCONTROLADORURL7286",
                    "471026");

    private final String key;
    private final String value;

    private FactorescesantiasControladorUrlEnum(String key, String value) {
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
