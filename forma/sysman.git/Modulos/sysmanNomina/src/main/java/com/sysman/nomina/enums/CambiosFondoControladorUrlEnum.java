/*
 * CambiosFondoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CambiosFondoControladorUrlEnum {

    URL0001("CAMBIOSFONDOCONTROLADORURL0001", "210016"),

    URL0002("CAMBIOSFONDOCONTROLADORURL0002", "210018"),

    URL18929("CAMBIOSFONDOCONTROLADORURL18929", "461004"),

    URL18074("CAMBIOSFONDOCONTROLADORURL18074", "475002");

    private final String key;
    private final String value;

    private CambiosFondoControladorUrlEnum(String key, String value) {
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
