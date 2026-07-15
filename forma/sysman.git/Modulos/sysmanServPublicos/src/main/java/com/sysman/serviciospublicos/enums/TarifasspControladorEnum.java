/*
 * TarifasspControladorEnum
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
public enum TarifasspControladorEnum {

    PARAM0("USO_ACTUAL"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_ANO("KEY_ANO"),

    KEY_PERIODO("KEY_PERIODO"),

    KEY_USO("KEY_USO"),

    KEY_ESTRATO("KEY_ESTRATO");

    private final String value;

    private TarifasspControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
