/*
 * FrmdesviacionesControladorEnum
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
public enum FrmdesviacionesControladorEnum {

    PARAM3("CODIGOFINAL"),

    PARAM4("FECHAINICAL"),

    PARAM1("CICLOFINAL"),

    PARAM2("CODIGOINICIAL"),

    PARAM0("CICLOINICIAL"),

    PARAM5("FECHAFINAL"),

    PARAM6("ITEM");

    private final String value;

    private FrmdesviacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
