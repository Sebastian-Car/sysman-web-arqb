/*
 * InvindvdevolutivosControladorUrlEnum
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
public enum InvindvdevolutivosControladorUrlEnum {

    URL3392("INVINDVDEVOLUTIVOSCONTROLADORURL3392", "112050"),

    URL2524("INVINDVDEVOLUTIVOSCONTROLADORURL2524", "112048"),

    URL149("INVINDVDEVOLUTIVOSCONTROLADORURL149", "5004");

    private final String key;
    private final String value;

    private InvindvdevolutivosControladorUrlEnum(String key, String value) {
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
