/*
 * LconceptosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum LconceptosControladorEnum {

    CODIGOINICIAL("CODIGOINICIAL"),
    REPORTE001039("001039LConceptos");

    private final String value;

    private  LconceptosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
