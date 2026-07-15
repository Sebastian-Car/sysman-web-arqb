/*
 * ConfigurarPlanPptalChipsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos
 * Map<String,String> y disponibles en dicha enumeración.
 */
public enum GeneraProcesoSiaControladorEnum {

    SUBTIPO("SUBTIPO"),

    CONSULTA("CONSULTA"),

    NOMBRE_ARCHIVO("NOMBRE_ARCHIVO"),
    
    COLUMNAS("COLUMNAS"),
    
    SEPARADOR("SEPARADOR"),
    
    VIGENCIAS_APPUI("VIGENCIAS_APPUI");

    private final String value;

    private GeneraProcesoSiaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
