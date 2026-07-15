/*
 * EntdevolutivoactivosControladorUrlEnum
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
public enum EntdevolutivoactivosControladorUrlEnum {

    URL5982("ENTDEVOLUTIVOACTIVOSCONTROLADORURL5982", "139009"),

    URL210("ENTDEVOLUTIVOACTIVOSCONTROLADORURL210", "161001"),

    URL224("ENTDEVOLUTIVOACTIVOSCONTROLADORURL224", "161002"),

    URL108("ENTDEVOLUTIVOACTIVOSCONTROLADORURL108", "139014");

    private final String key;
    private final String value;

    private EntdevolutivoactivosControladorUrlEnum(String key, String value) {
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
