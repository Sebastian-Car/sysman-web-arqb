/*
 * FrmInversionxAnoControladorEnum
 *
 * 1.0
 *
 * 20/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmInversionxAnoControladorEnum {

    NIVEL("NIVEL"),

    ANIO("ANIO"),

    ANIO_FINAL("ANIO_FINAL");

    private final String value;

    private FrmInversionxAnoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
