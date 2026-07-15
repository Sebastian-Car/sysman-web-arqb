/*
 * InventariosumControladorUrlEnum
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
public enum InventariosumControladorUrlEnum {

    URL4263("INVENTARIOCONTROLADORURL4263",
                    "112034"),

    URL5117("INVENTARIOSUMCONTROLADORURL5117",
                    "62017"),

    URL3619("INVENTARIOSUMCONTROLADORURL3619",
                    "112032"),

    URL5671("INVENTARIOSUMCONTROLADORURL5671",
                    "62019");

    private final String key;
    private final String value;

    private InventariosumControladorUrlEnum(String key, String value) {
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
