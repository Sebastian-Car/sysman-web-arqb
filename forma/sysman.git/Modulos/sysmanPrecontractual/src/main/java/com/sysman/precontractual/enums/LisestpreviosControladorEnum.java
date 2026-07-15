/*
 * LisestpreviosControladorEnum
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
public enum LisestpreviosControladorEnum {

    COD_T_CONTRATO("COD_T_CONTRATO"),

    CONTRATO("CONTRATO"),

    REPORTE416("000416GRALESTPREVIOS"),

    MODALIDADINICIALLOWER("modalidadInicial"),

    MODALIDADFINALLOWER("modalidadFinal"),

    FECHAINICIALLOWER("fechaInicial"),

    FECHAFINALLOWER("fechaFinal"),

    PR_STRSQL("PR_STRSQL");

    private final String value;

    private LisestpreviosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
