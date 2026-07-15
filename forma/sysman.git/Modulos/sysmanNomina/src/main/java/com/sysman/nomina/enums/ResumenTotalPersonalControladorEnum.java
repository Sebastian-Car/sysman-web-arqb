/*
 * ResumenTotalPersonalControladorEnum
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
public enum ResumenTotalPersonalControladorEnum {

    PARAM3("PARAM3"),

    PERIODO2("PERIODO2"),

    PROCESO("PROCESO"),

    ANO1("ANO1"),

    ID_PROCESO("ID_PROCESO"),

    MES1("MES1"),

    PERIODO1("PERIODO1"),

    ANO2("ANO2"),

    MES2("MES2");

    private final String value;

    private ResumenTotalPersonalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
