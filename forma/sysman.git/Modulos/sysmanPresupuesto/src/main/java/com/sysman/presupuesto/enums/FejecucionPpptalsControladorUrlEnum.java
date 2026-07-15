/*
 * FejecucionPpptalsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FejecucionPpptalsControladorUrlEnum {

    URL6064("FEJECUCIONPPPTALSCONTROLADORURL6064", "94036"),

    URL7124("FEJECUCIONPPPTALSCONTROLADORURL7124", "94034"),

    URL5447("FEJECUCIONPPPTALSCONTROLADORURL5447", "7001"),

    URL5047("FEJECUCIONPPPTALSCONTROLADORURL5047", "4001");

    private final String key;
    private final String value;

    private FejecucionPpptalsControladorUrlEnum(String key, String value) {
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
