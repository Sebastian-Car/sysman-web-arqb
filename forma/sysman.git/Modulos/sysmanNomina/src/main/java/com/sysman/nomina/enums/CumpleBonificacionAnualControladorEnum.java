/*
 * CumpleBonificacionAnualControladorEnum
 *
 * 1.0
 *
 * 05/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum CumpleBonificacionAnualControladorEnum {

    PROCESO("PROCESO"),

    CB("CB");

    private final String value;

    private CumpleBonificacionAnualControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
