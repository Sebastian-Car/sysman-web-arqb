/*
 * RetencionsControladorUrlEnum
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
public enum RetencionsControladorUrlEnum {

    URL5727("RETENCIONSCONTROLADORURL5727", "11002"),

    URL2312("RETENCIONSCONTROLADORURL2312", "11001"),

    URL2703("RETENCIONSCONTROLADORURL2703", "4001");

    private final String key;
    private final String value;

    private RetencionsControladorUrlEnum(String key, String value) {
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
