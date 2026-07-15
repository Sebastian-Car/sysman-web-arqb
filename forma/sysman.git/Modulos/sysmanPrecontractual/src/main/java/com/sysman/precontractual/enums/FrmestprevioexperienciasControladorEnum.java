/*
 * FrmestprevioexperienciasControladorEnum
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
public enum FrmestprevioexperienciasControladorEnum {

    PYE_CONTRATISTA("PYE_CONTRATISTA"),

    ES_ESTPREVIO("ES_ESTPREVIO"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_COD_ESTUDIO("KEY_COD_ESTUDIO"),

    ES_ESTPREVIO_EXPERIENCIA("ES_ESTPREVIO_EXPERIENCIA");

    private final String value;

    private FrmestprevioexperienciasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
