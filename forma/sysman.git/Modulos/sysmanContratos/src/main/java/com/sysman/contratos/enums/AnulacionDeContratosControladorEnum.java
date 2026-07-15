/*
 * AnulacionDeContratosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum AnulacionDeContratosControladorEnum {

    PARAM1("AUXTIPO"),

    PARAM0("CLASEORDEN"),

    PARAM2("NUMERO");

    private final String value;

    private AnulacionDeContratosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
