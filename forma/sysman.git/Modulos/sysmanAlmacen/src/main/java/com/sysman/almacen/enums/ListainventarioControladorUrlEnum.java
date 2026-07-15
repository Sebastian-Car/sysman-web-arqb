/*
 * ListainventarioControladorUrlEnum
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
public enum ListainventarioControladorUrlEnum {

    URL3765("LISTAINVENTARIOCONTROLADORURL3765",
                    "112034"),

    URL2977("LISTAINVENTARIOCONTROLADORURL2977",
                    "112032");

    private final String key;
    private final String value;

    private ListainventarioControladorUrlEnum(String key, String value) {
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
