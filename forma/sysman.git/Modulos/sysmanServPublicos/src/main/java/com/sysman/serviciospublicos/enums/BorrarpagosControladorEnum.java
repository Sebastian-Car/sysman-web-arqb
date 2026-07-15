/*
 * BorrarpagosControladorEnum
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
public enum BorrarpagosControladorEnum {

    PARAM4("NUMEROPAQUETE"),
    
    PARAM3("BANCO"),

    PARAM1("CONDICIONFECHA"),

    PARAM2("FECHA"),

    PARAM0("CONDICIONUSUARIO");

    private final String value;

    private BorrarpagosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
