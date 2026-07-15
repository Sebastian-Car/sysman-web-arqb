/*
 * PactualplancomprasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum PactualplancomprasControladorEnum {

    PARAM2("VALOR_UNITARIO"),
    PARAM1("FUENTE_DE_RECURSOS"), 
    PARAM0("ACTUALIZACION");

    private final String value;

    private  PactualplancomprasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
