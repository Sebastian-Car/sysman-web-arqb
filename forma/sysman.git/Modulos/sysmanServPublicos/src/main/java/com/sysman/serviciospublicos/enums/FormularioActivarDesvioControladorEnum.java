/*
 * LecturaCriticaInfControladorEnum
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
public enum FormularioActivarDesvioControladorEnum {

    PARAM0("VENCIMIENTO"),

    PARAM1("LECTURAINICIAL"),

    PARAM2("FECHAAFORO"),

    PARAM3("PROMEDIO"),

    PARAM4("LECTURAANTERIOR"),

    PARAM5("FECHACREADA");

    private final String value;

    private FormularioActivarDesvioControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
