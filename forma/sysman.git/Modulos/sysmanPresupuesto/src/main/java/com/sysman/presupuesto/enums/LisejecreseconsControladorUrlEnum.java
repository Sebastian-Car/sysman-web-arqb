/*
 * LisejecreseconsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LisejecreseconsControladorUrlEnum {

    URL4084("LISEJECRESECONSCONTROLADORURL4084", "94102"),

    URL3655("LISEJECRESECONSCONTROLADORURL3655", "4001"),

    URL4833("LISEJECRESECONSCONTROLADORURL4833", "94104");

    private final String key;
    private final String value;

    private LisejecreseconsControladorUrlEnum(String key, String value) {
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
