/*
 * CrearAcuerdosControladorUrlEnum
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
public enum CrearAcuerdosControladorUrlEnum {

    URL10838("CREARACUERDOSCONTROLADORURL10838",
                    "385005"),

    URL21478("CREARACUERDOSCONTROLADORURL21478",
                    "374003"),

    URL11795("CREARACUERDOSCONTROLADORURL11795",
                    "385006"),

    URL36022("CREARACUERDOSCONTROLADORURL36022",
                    "385004"),

    URL34735("CREARACUERDOSCONTROLADORURL34735",
                    "371005"),

    URL20280("CREARACUERDOSCONTROLADORURL20280",
                    "385003"),

    URL13585("CREARACUERDOSCONTROLADORURL13585",
                    "385007");

    private final String key;
    private final String value;

    private CrearAcuerdosControladorUrlEnum(String key, String value) {
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
