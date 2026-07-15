/*
 * SolicitudDisponibilidadControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SolicitudDisponibilidadControladorEnum {

    IMPRESO("IMPRESO"),

    CODEXCLUIDO("CODIGOEXCLUIDO"),
    
    BPIN("BPIN"),
    
    COFINANCIACION("COFINANCIACION"),
    
    FUNCIONAMIENTO("FUNCIONAMIENTO"),
    
    PROPIOS("PROPIOS"),
    
    TIPO_COMPROMISO("TIPO_COMPROMISO"),
    
    NOMBRE_COMPROMISO("NOMBRE_COMPROMISO");

    private final String value;

    private SolicitudDisponibilidadControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
