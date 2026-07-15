/*
 * LsubsobrepreciosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum LsubsobrepreciosControladorEnum {

    CICLOINICIAL("CICLOINICIAL"),

    CICLOFINAL("CICLOFINAL"),

    CODIGOINICIAL("CODIGOINICIAL"),

    PARAM0("CODIGO_INICIAL");

    private final String value;

    private LsubsobrepreciosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
