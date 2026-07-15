/*
 * CumpleBonificacionAnualControladorEnum
 *
 * 1.0
 *
 * 05/09/2017
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
public enum CumplequinquenioControladorEnum {

    MES_NOMINA_LOWER("mesNomina"),

    MES_LOWER("mes"),

    REPORTE438("000438CumpleQuinquenio"),

    PR_STRSQL("PR_STRSQL"),

    PR_MES1("PR_MES1"),

    PR_NOMBREEMPRESA("PR_NOMBREEMPRESA");

    private final String value;

    private CumplequinquenioControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
