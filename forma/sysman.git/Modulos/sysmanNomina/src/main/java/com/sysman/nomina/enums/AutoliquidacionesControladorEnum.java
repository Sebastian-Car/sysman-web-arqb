/*
 * AutoliquidacionesControladorEnum
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
public enum AutoliquidacionesControladorEnum {

    MESNOMINA("mesNomina"),

    KEY_NIT("KEY_NIT"),

    KEY_COMPANIA("KEY_COMPANIA"),

    FONDORIESGOS("FONDORIESGOS"),

    NIT("NIT");

    private final String value;

    private AutoliquidacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
