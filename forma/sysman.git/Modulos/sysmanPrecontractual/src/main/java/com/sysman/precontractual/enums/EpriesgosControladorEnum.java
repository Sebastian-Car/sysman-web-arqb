/*
 * EpriesgosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum EpriesgosControladorEnum {

    COD_RIESGO("COD_RIESGO"),

    RIESGO("RIESGO"),

    COD_T_RIESGO("COD_T_RIESGO"),

    TRIESGO("TRIESGO");

    private final String value;

    private EpriesgosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
