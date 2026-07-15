/*
 * LsubsobrelisControladorEnum
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
public enum LsubsobreControladorEnum {

    PARAM5("CODIGOUSO"),

    PARAM4("CODIGOFINAL"),

    PARAM3("CODIGOINICIAL"),

    PARAM1("CICLOINICIAL"),

    PARAM2("CICLOFINAL"),

    PARAM0("CODIGO_INICIAL"),

    PARAM6("PARSUBI"),
    
    PARAM7("NUMEROINICIAL");

    private final String value;

    private LsubsobreControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
