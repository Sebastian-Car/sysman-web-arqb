/*
 * AnulacionDeContratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AnulacionDeContratosControladorUrlEnum {

    URL2589("ANULACIONDECONTRATOSCONTROLADORURL2589", "82043"),

    URL2158("ANULACIONDECONTRATOSCONTROLADORURL2158", "73016"),

    URL4681("ANULACIONDECONTRATOSCONTROLADORURL4681", "82045");

    private final String key;
    private final String value;

    private AnulacionDeContratosControladorUrlEnum(String key, String value) {
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
