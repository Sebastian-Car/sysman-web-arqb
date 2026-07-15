/*
 * SubfrmanulacionsaldoscreditosControladorUrlEnum
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
public enum SubfrmanulacionsaldoscreditosControladorUrlEnum {

    URL4684("SUBFRMANULACIONSALDOSCREDITOSCONTROLADORURL4684",
                    "386017"),

    URL5749("SUBFRMANULACIONSALDOSCREDITOSCONTROLADORURL5749",
                    "386019"),

    URL1515("SUBFRMANULACIONSALDOSCREDITOSCONTROLADORURL1515", 
                    "386015");

    private final String key;
    private final String value;

    private SubfrmanulacionsaldoscreditosControladorUrlEnum(String key,
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
