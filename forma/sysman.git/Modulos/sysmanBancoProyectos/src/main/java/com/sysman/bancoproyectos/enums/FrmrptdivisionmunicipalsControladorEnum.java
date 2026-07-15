/*
 * FrmrptdivisionmunicipalsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmrptdivisionmunicipalsControladorEnum {

    PARAM5("PARAM5"),

    CIUDAD("CIUDAD"),

    PAIS("PAIS"),

    DEPARTAMENTO("DEPARTAMENTO"),

    REPORTE302("000302rptBarriosconAsociaciones"),

    REPORTE306("000306rptBarrios"),

    REPORTE307("000307rptciudades"),

    CIUDAD_LOWER("ciudad"),

    PAIS_LOWER("pais"),

    DEPARTAMENTO_LOWER("departamento"),

    PR_PAIS("PR_PAIS")

    ;

    private final String value;

    private FrmrptdivisionmunicipalsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
