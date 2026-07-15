/*
 * SdisponibilidadsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SdisponibilidadsControladorUrlEnum {

    URL3340("SDISPONIBILIDADSCONTROLADORURL3340", "110010"),

    URL2851("SDISPONIBILIDADSCONTROLADORURL2851", "109022");

    private final String key;
    private final String value;

    private SdisponibilidadsControladorUrlEnum(String key, String value) {
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
