/*
 * ControlAccesoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ControlAccesoControladorUrlEnum {

    URL3966("CONTROLACCESOCONTROLADORURL3966",
                    "59021"),

    URL3675("CONTROLACCESOCONTROLADORURL3675",
                    "47021"),

    URL6879("CONTROLACCESOCONTROLADORURL6879",
                    "1052001"),

    URL8585("CONTROLACCESOCONTROLADORURL6879",
                    "1052002");

    private final String key;
    private final String value;

    private ControlAccesoControladorUrlEnum(String key, String value) {
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
