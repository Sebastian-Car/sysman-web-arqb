/*
 * DiabloqueosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum DiabloqueosControladorEnum {

    PARAM0("APLICACION"),

    PARAM1("DIA"),

    PARAM2("PROCESO"),

    PARAM3("PROCESO_NOMBRE"),

    PARAM4("NOMBREESTADO");

    private final String value;

    private DiabloqueosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
