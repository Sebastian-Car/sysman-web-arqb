/*
 * LsubsobrepreciosControladorUrlEnum
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
public enum LsubsobrepreciosControladorUrlEnum {

    URL001("LSUBSOBREPRECIOSCONTROLADORURL001", "59010"),

    URL002("LSUBSOBREPRECIOSCONTROLADORURL002", "213206"),

    URL003("LSUBSOBREPRECIOSCONTROLADORURL003", "213208"),

    URL18392("LSUBSOBREPRECIOSCONTROLADORURL18392", "214020"),

    URL19055("LSUBSOBREPRECIOSCONTROLADORURL19055", "214024");

    private final String key;
    private final String value;

    private LsubsobrepreciosControladorUrlEnum(String key, String value) {
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
