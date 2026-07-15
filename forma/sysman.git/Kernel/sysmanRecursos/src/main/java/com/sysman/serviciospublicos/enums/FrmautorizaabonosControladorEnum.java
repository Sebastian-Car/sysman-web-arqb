/*
 * FrmautorizaabonosControladorEnum
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
public enum FrmautorizaabonosControladorEnum {

    PARAM5("VALORANTFACT"),

    PARAM4("FACTAUTORIZADA"),

    PARAM3("FECHAPROCESO"),

    PARAM1("CODIGOINTERNO"),

    PARAM2("ABONO"),

    PARAM0("CICLO");

    private final String value;

    private FrmautorizaabonosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
