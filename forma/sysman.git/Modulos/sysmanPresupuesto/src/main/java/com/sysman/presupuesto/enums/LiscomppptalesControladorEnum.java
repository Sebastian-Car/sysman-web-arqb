/*
 * LiscomppptalesControladorEnum
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
public enum LiscomppptalesControladorEnum {

    PARAM0("CODIGOINICIAL"),

    PARAM1("TIPOINICIAL"),

    PARAM2("TIPOFINAL");

    private final String value;

    private LiscomppptalesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
