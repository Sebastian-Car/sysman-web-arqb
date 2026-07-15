/*
 * InformepaasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum InformepaasControladorEnum {

    REPORTE457("000457PlanAnualDeAdquisiciones"),

    SUB458("000458SubPAA"),

    ANIO_LOWER("anio"),

    PR_STRSQL("PR_STRSQL"),

    PR_STRSQL_SUB_PAA("PR_STRSQL_SUB_PAA"),

    PR_DESCRIPCIONA("PR_DESCRIPCIONA"),

    PR_CODIGOA("PR_CODIGOA"),

    PR_RESPONSABLEA("PR_RESPONSABLEA");

    private final String value;

    private InformepaasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
