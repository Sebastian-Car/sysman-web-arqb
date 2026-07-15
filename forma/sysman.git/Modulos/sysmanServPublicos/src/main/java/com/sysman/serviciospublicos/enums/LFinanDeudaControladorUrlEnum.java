/*
 * LFinanDeudaControladorUrlEnum
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
public enum LFinanDeudaControladorUrlEnum {

    URL6852("LFINANDEUDACONTROLADORURL6852", "227001"),

    URL6289("LFINANDEUDACONTROLADORURL6289", "214081"),

    URL8572("LFINANDEUDACONTROLADORURL8572", "227034"),

    URL7896("LFINANDEUDACONTROLADORURL7896", "227003"),

    URL7298("LFINANDEUDACONTROLADORURL7298", "227033");

    private final String key;
    private final String value;

    private LFinanDeudaControladorUrlEnum(String key, String value) {
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
