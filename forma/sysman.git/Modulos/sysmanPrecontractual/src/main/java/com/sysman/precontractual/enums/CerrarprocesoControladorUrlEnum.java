/*
 * CerrarprocesoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CerrarprocesoControladorUrlEnum {

    URL3357("CERRARPROCESOCONTROLADORURL3357", "188003"),

    URL2854("CERRARPROCESOCONTROLADORURL2854", "184003");

    private final String key;
    private final String value;

    private CerrarprocesoControladorUrlEnum(String key, String value) {
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
