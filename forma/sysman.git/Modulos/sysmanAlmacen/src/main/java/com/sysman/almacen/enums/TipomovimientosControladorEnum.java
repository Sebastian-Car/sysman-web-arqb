/*
 * TipomovimientosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum TipomovimientosControladorEnum {

    PARAM4("BODEGADESTINO"),

    PARAM3("BODEGAORIGEN"),

    PARAM2("CLASE"),

    PARAM1("CONCEPTO"),

    PARAM0("MOVIMIENTO");

    private final String value;

    private TipomovimientosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
