/*
 * SubpolizasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum SubpolizasControladorEnum {

    /**
     * Par&aacute;metro TIPO.
     */
    TIPO("TIPO"),

    /**
     * Par&aacute;metro LLAVE.
     */
    LLAVE("LLAVE"),

    /**
     * Par&aacute;metro INDIMPRESION.
     */
    INDIMPRESION("INDIMPRESION"),

    /**
     * Par&aacute;metro ORDENDECOMPRA.
     */
    ORDENDECOMPRA("ORDENDECOMPRA");

    private final String value;

    private SubpolizasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
