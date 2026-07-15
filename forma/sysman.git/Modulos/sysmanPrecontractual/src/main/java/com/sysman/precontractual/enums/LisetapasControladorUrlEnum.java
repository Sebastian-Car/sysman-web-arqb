/*
 * LisetapasControladorUrlEnum
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
public enum LisetapasControladorUrlEnum {

    URL2748("LISETAPASCONTROLADORURL2748", "184003"),

    URL3299("LISETAPASCONTROLADORURL3299", "497005"),

    URL4062("LISETAPASCONTROLADORURL4062", "497006");

    private final String key;
    private final String value;

    private LisetapasControladorUrlEnum(String key, String value) {
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
