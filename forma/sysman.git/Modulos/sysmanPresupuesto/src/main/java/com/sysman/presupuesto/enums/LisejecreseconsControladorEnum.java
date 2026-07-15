/*
 * LisejecreseconsControladorEnum
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
public enum LisejecreseconsControladorEnum {

    PARAM2("PARAM2"), PARAM1("PARAM1"), PARAM4("CUENTAINICIAL"), PARAM3(
                    "PARAM3"), PARAM0("PARAM0");

    private final String value;

    private LisejecreseconsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
