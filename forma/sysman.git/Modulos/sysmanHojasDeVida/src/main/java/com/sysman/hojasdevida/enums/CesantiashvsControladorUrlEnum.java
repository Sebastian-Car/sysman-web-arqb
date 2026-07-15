/*
 * CalificacionControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CesantiashvsControladorUrlEnum {

    URL001("CESANTIASHVSCONTROLADORURLENUMURL001", "744002"),

    URL002("CESANTIASHVSCONTROLADORURLENUM6230URL002", "744004"),

    URL003("CESANTIASHVSCONTROLADORURLENUM6230URL003", "744004"),

    URL004("CESANTIASHVSCONTROLADORURLENUM6230URL004", "629007");

    private final String key;
    private final String value;

    private CesantiashvsControladorUrlEnum(String key,
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
