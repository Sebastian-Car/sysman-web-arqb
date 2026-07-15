/*
 * PrediallisnoticobroControladorUrlEnum
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
public enum PrediallisnoticobroControladorUrlEnum {

    URL3743("PREDIALLISNOTICOBROCONTROLADORURL3743", "367086"),

    URL4746("PREDIALLISNOTICOBROCONTROLADORURL4746", "367088");

    private final String key;
    private final String value;

    private PrediallisnoticobroControladorUrlEnum(String key, String value) {
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
