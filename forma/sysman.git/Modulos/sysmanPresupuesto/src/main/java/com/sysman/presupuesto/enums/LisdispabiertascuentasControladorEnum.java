/*
 * LisdispabiertascuentasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum LisdispabiertascuentasControladorEnum {
    /**
     * Parametro CODIGOINICIAL
     */
    PARAM4("CODIGOINICIAL"),
    /**
     * Parametro CUENTAFINAL
     */
    PARAM3("CUENTAFINAL"),
    /**
     * Parametro CUENTAINICIAL
     */
    PARAM2("CUENTAINICIAL"),
    /**
     * Parametro ANO
     */
    PARAM1("ANO"),
    /**
     * Parametro COMPANIA
     */
    PARAM0("COMPANIA"),
    /**
     * Parametro ANOINICIAL
     */
    PARAM5("ANOINICIAL"),
    /**
     * Parametro ANOFINAL
     */
    PARAM6("ANOFINAL");

    private final String value;

    private LisdispabiertascuentasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
