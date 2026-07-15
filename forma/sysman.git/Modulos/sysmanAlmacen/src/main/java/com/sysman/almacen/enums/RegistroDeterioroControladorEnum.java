/*
 * ProveedorControladorEnum
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
public enum RegistroDeterioroControladorEnum {

    VALOR_LIBROS("VALOR_LIBROS"),

    CODIGOELEMENTO("CODIGOELEMENTO"),

    DETERIORO("DETERIORO"),

    FECHACAMBIO("FECHACAMBIO"),

    NIIF_VALOR_TOTAL("NIIF_VALOR_TOTAL"),

    NIIF_VLRLIBROS("NIIF_VLRLIBROS"),

    VIDAUTIL("VIDAUTIL"),

    NOMBRELARGO("NOMBRELARGO");

    private final String value;

    private RegistroDeterioroControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
