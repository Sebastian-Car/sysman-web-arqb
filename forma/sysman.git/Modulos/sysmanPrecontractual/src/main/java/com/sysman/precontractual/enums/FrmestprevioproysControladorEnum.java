/*
 * FrmestprevioproysControladorEnum
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
public enum FrmestprevioproysControladorEnum {

    PR_FRM_ORIGEN("PR_FRM_ORIGEN"),

    NUM_PROCESO("NUM_PROCESO"),

    TIPOCONTRATO("TIPOCONTRATO"),

    ID("ID"),

    LISTAMODELOS("LISTAMODELOS"),

    CEDULA("CEDULA"),

    MODULO("MODULO"),

    CODCONTRATO("CODCONTRATO"),

    TIPO("TIPO"),

    COD_T_CONTRATO("COD_T_CONTRATO");

    private final String value;

    private FrmestprevioproysControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
