/*
 * FrmViaticosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmViaticosControladorEnum {

    DEPARTAMENTO("DEPARTAMENTO"),

    CODSOLICITUD("CODSOLICITUD"),

    FECHAINICIO("FECHAINICIO"),

    FECHAFIN("FECHAFIN"),

    MISIONAL("MISIONAL"),

    ESCALAFON("ESCALAFON"),

    NUMSOLICITUD("NUMSOLICITUD"),
    
    CODTIPO("CODTIPO"),
    
    TSOLICITUD("TSOLICITUD");

    private final String value;

    private FrmViaticosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
