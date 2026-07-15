/*
 * ProponenteetapasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ProponenteetapasControladorUrlEnum {

    URL19279("PROPONENTEETAPASCONTROLADORURL19279", "520005"),

    URL17930("PROPONENTEETAPASCONTROLADORURL17930", "534001"),

    URL16328("PROPONENTEETAPASCONTROLADORURL16328", "533004"),

    URL6155("PROPONENTEETAPASCONTROLADORURL6155", "14115"),

    URL7571("PROPONENTEETAPASCONTROLADORURL7571", "527001"),

    URL7572("PROPONENTEETAPASCONTROLADORURL7572", "532002");

    private final String key;
    private final String value;

    private ProponenteetapasControladorUrlEnum(String key, String value) {
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
