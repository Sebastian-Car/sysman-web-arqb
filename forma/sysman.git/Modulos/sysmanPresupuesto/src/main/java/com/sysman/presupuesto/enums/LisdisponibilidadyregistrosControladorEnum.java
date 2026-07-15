/*
 * LisdisponibilidadyregistrosControladorEnum
 *
 * 1.0
 *
 * 30/11/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum LisdisponibilidadyregistrosControladorEnum {

    CLASES("CLASES");

    private final String value;

    private LisdisponibilidadyregistrosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
