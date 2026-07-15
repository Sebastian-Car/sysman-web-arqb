/*
 * RevisarcuentasporpagarsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RevisarcuentasporpagarsControladorUrlEnum {

    URL5730("REVISARCUENTASPORPAGARSCONTROLADORURL5730", "72017"),

    URL5729("REVISARCUENTASPORPAGARSCONTROLADORURL5729", "72025"),

    URL5728("REVISARCUENTASPORPAGARSCONTROLADORURL5728", "72027"),

    URL5727("REVISARCUENTASPORPAGARSCONTROLADORURL5727", "7200D");

    private final String key;
    private final String value;

    private RevisarcuentasporpagarsControladorUrlEnum(String key,
        String value) {
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
