/*
 * InvdevolinicialControladorUrlEnum
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
public enum InvdevolinicialControladorUrlEnum {

    URL5279("INVDEVOLINICIALCONTROLADORURL5279",
                    "61012"),

    URL6206("INVDEVOLINICIALCONTROLADORURL6206",
                    "61014"),

    URL4105("INVDEVOLINICIALCONTROLADORURL4105",
                    "112038"),

    URL3050("INVDEVOLINICIALCONTROLADORURL3050",
                    "112036");

    private final String key;
    private final String value;

    private InvdevolinicialControladorUrlEnum(String key, String value) {
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
