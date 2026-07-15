/*
 * InvgraldevoluControladorUrlEnum
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
public enum InvgraldevoluControladorUrlEnum {

    URL3250("INVGRALDEVOLUCONTROLADORURL3250", "112044"),

    URL4125("INVGRALDEVOLUCONTROLADORURL4125", "112046");

    private final String key;
    private final String value;

    private InvgraldevoluControladorUrlEnum(String key, String value) {
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
