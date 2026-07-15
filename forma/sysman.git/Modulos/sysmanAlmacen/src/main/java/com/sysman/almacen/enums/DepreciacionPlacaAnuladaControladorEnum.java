/*
 * DepreciacionPlacaAnuladaControladorEnum
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
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum DepreciacionPlacaAnuladaControladorEnum {

    /**
     * Par&aacute;metro CODIGOINICIAL
     */
    CODIGOINICIAL("CODIGOINICIAL"),

    /**
     * Par&aacute;metro ELEMENTOINICIAL
     */
    ELEMENTOINICIAL("ELEMENTOINICIAL");

    private final String value;

    private DepreciacionPlacaAnuladaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
