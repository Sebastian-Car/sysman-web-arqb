/*
 * SubirSaldosInicialesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum RetencionesControladorEnum {

    PCT_APLICARLEY1607("PCT_APLICARLEY1607"),

    ANOPREPARAR("ANOPREPARAR"),

    PREPARAANO("PREPARAANO");

    private final String value;

    private RetencionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
