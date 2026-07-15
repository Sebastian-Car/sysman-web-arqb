/*
 * RegistroprescripcionesControladorUrlEnum
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
public enum RegistroprescripcionesControladorUrlEnum {

    URL5072("REGISTROPRESCRIPCIONESCONTROLADORURL5072", "104034"),

    URL4302("REGISTROPRESCRIPCIONESCONTROLADORURL4302", "367190"),

    URL4303("REGISTROPRESCRIPCIONESCONTROLADORURL4303", "4001");

    private final String key;
    private final String value;

    private RegistroprescripcionesControladorUrlEnum(String key, String value) {
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
