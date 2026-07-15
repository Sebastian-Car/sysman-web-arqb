/*
 * RelcontratosvencerControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum RelcontratosvencerControladorEnum {

    PARAM0("SOLO CONTRATOS ACTIVOS EN INFORME CONTRATOS POR VENCER"),

    PARAM1("ACTIVO"),

    PARAM2("CODIGOINICIAL"),

    PARAM3("TECEROINICIAL"),

    PARAM4("NIT"),

    PARAM5("KEY_COMPANIA"),

    PARAM6("KEY_NOMBRE"),

    PARAM7("RELACI�N DE CONTRATOS POR VENCER"),

    PARAM8("KEY_MODULO"),

    PARAM9("-1"),

    PARAM10("DATE_MODIFIED"),

    PARAM11("MODIFIED_BY"),

    PARAM12("000277RelContratosVencer"),

    PARAM13(""),

    PARAM14(""),

    PARAM15(""),;

    private final String value;

    private RelcontratosvencerControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
