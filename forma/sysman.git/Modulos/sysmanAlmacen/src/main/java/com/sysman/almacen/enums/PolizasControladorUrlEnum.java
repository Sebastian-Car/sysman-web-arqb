/*
 * PolizasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PolizasControladorUrlEnum {

    URL7194("POLIZASCONTROLADORURL7194", "95003"),

    URL8175("POLIZASCONTROLADORURL8175", "141063"),

    URL7627("POLIZASCONTROLADORURL7627", "141061"),

    URL9262("POLIZASCONTROLADORURL9262", "141065"),
    
    URL168001("POLIZASCONTROLADORURL168001", "168001");

    private final String key;
    private final String value;

    private PolizasControladorUrlEnum(String key, String value) {
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
