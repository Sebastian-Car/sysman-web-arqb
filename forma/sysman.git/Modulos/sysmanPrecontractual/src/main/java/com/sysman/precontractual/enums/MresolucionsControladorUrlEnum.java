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
public enum MresolucionsControladorUrlEnum {

    URL3357("MRESOLUCIONSCONTROLADORURL3357", "111014"),

    URL2854("MRESOLUCIONSCONTROLADORURL2854", "104045");

    private final String key;
    private final String value;

    private MresolucionsControladorUrlEnum(String key, String value) {
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
