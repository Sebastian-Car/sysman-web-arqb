/*
 * LisReoAbiertosCtasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum LisReoAbiertosCtasControladorEnum {

    PARAM6("TERCEROINICIAL"),

    PARAM5("PARAM5"),

    PARAM7("PARAM7"),

    PARAM2("CUENTAINICIAL"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("PARAM0");

    private final String value;

    private LisReoAbiertosCtasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
