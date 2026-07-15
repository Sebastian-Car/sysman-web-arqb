/*
 * RepresentanteComisionPersonalControladorUrlEnum
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
public enum RepresentanteComisionPersonalControladorUrlEnum {

    URL9717("REPRESENTANTECOMISIONPERSONALCONTROLADORURL9717", "785001"),

    URL7613("REPRESENTANTECOMISIONPERSONALCONTROLADORURL7613", "210117"),

    URL8603("REPRESENTANTECOMISIONPERSONALCONTROLADORURL8603", "987001");

    private final String key;
    private final String value;

    private RepresentanteComisionPersonalControladorUrlEnum(String key,
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
