/*
 * FrmsaldosxaplicarControladorUrlEnum
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
public enum FrmsaldosxaplicarControladorUrlEnum {

    URL3909("FRMSALDOSXAPLICARCONTROLADORURL3909", "367051"),

    URL4836("FRMSALDOSXAPLICARCONTROLADORURL4836", "367053");

    private final String key;
    private final String value;

    private FrmsaldosxaplicarControladorUrlEnum(String key, String value) {
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
