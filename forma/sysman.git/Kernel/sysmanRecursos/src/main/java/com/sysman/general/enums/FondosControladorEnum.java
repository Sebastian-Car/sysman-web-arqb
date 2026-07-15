/*
 * FondosControladorEnum
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
public enum FondosControladorEnum {

    PARAM3("CLASEFONDO"),

    PARAM4("PAIS"),

    PARAM1("FONDO"),

    PARAM0("KEY_COMPANIA"),

    PARAM5("KEY_ID"),

    PARAM6("CLASE"),

    AFP("AFP"),

    EPS("EPS"),

    RETORNO("retorno"),
    
    DCTO_IDENTIDAD("DCTO_IDENTIDAD"),
    
    DOC_SIIF("DOC_SIIF");

    private final String value;

    private FondosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
