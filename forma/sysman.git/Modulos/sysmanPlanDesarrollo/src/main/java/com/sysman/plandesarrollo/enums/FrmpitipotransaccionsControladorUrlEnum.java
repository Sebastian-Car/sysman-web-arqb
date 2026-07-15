/*
 * FrmpitipotransaccionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.plandesarrollo.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmpitipotransaccionsControladorUrlEnum {

    URL4711("FRMPITIPOTRANSACCIONSCONTROLADORURL4711", "1016001"),

    URL5242("FRMPITIPOTRANSACCIONSCONTROLADORURL5242", "1016001");

    private final String key;
    private final String value;

    private FrmpitipotransaccionsControladorUrlEnum(String key, String value) {
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
