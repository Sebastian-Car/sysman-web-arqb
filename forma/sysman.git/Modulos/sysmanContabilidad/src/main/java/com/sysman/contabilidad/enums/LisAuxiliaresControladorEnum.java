/*
 * LisAuxiliaresControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum LisAuxiliaresControladorEnum {

    TIPOINICIAL("TIPOINICIAL"),

    CODIGOINICIAL("CODIGOINICIAL");

    private final String value;

    private LisAuxiliaresControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
