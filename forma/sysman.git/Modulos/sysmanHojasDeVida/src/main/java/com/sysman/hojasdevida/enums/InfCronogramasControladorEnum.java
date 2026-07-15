/*
 * InfCronogramasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum InfCronogramasControladorEnum {

    CODIGO_ACTIVIDAD("CODIGO_ACTIVIDAD"),

    RESPONSABLEI("RESPONSABLEI");

    private final String value;

    private InfCronogramasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
