/*
 * AnularCertValControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AnularCertValControladorUrlEnum {

    URL4617("ANULARCERTVALCONTROLADORURL4617",
                    "412004"),

    URL3897("ANULARCERTVALCONTROLADORURL3897",
                    "412002"),
    
    URL2857("ANULARCERTVALCONTROLADORURL2857",
                    "412006");

    private final String key;
    private final String value;

    private AnularCertValControladorUrlEnum(String key, String value) {
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
