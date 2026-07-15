/*
 * LTomaLecturasControladorUrlEnum
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
public enum LTomaLecturasControladorUrlEnum {
    URL4823("LTOMALECTURASCONTROLADORURL4823", "214060"),

    URL6416("LTOMALECTURASCONTROLADORURL6416", "366004"),

    URL6829("LTOMALECTURASCONTROLADORURL6829", "366006");

    private final String key;
    private final String value;

    private LTomaLecturasControladorUrlEnum(String key, String value) {
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
