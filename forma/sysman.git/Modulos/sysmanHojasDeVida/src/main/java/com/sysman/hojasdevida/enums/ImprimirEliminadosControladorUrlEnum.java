/*
 * ImprimirEliminadosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ImprimirEliminadosControladorUrlEnum {

    URL4582("IMPRIMIRELIMINADOSCONTROLADORURL4582", "689005"),

    URL3853("IMPRIMIRELIMINADOSCONTROLADORURL3853", "708005");

    private final String key;
    private final String value;

    private ImprimirEliminadosControladorUrlEnum(String key, String value) {
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
