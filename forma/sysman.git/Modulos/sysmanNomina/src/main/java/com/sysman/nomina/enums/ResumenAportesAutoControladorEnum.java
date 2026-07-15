/*
 * ResumenAportesAutoControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ResumenAportesAutoControladorEnum {

    PARAM3("PARAM3"),

    PARAM4("PARAM4"),

    ID_PROCESO("ID_PROCESO"),

    PARAM2("PARAM2"),

    PROCESO("PROCESO");

    private final String value;

    private ResumenAportesAutoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
