/*
 * LsubsobreControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LsubsobreControladorUrlEnum {

    URL7971("LSUBSOBRECONTROLADORURL7971", "213159"),

    URL6537("LSUBSOBRECONTROLADORURL6537", "213160"),

    URL7330("LSUBSOBRECONTROLADORURL7330", "214022"),

    URL7331("LSUBSOBRECONTROLADORURL7331", "214088"),

    URL8587("LSUBSOBRECONTROLADORURL8587", "213206"),

    URL8588("LSUBSOBRECONTROLADORURL8587", "213208"),

    URL5717("LSUBSOBRECONTROLADORURL5717", "214060"),

    URL5718("LSUBSOBRECONTROLADORURL5718", "214088"),

    URL9304("LSUBSOBRECONTROLADORURL9304", "213161"),

    URL9354("LSUBSOBRECONTROLADORURL9354", "213162"),

    URL9355("LSUBSOBRECONTROLADORURL9354", "214090");

    private final String key;
    private final String value;

    private LsubsobreControladorUrlEnum(String key, String value) {
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
