/*
 * FrminfooperacionControladorEnum
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
public enum FrminfooperacionControladorEnum {

    MODULO("MODULO"),

    TIPO("TIPO"),

    MI_TIPOS("MI_TIPOS"),

    MI_CICLO("MI_CICLO"),

    MI_ANIO("MI_ANIO"),

    MI_PERIODO("MI_PERIODO"),

    TOTALFINANCIABLE("TOTALFINANCIABLE"),

    CAMPO("#CAMPO#"),

    ESTADO("#ESTADO#"),

    USUARIO("#USUARIO#"),

    ESTADO_OPE("ESTADO_OPE"),

    TIPO_OPERACION("TIPO_OPERACION"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("PARAM0");

    private final String value;

    private FrminfooperacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
