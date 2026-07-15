/*
 * IndprocesodecobroControladorUrlEnum
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
public enum IndprocesodecobroControladorUrlEnum {

    URL11637("INDPROCESODECOBROCONTROLADORURL11637",
                    "367115"),

    URL6070("INDPROCESODECOBROCONTROLADORURL6070",
                    "367114"),

    URL4416("INDPROCESODECOBROCONTROLADORURL4416",
                    "367112");

    private final String key;
    private final String value;

    private IndprocesodecobroControladorUrlEnum(String key, String value) {
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
