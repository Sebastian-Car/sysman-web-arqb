/*
 * FrmInversionCuatrenioControladorEnum
 *
 * 1.0
 *
 * 19/09/2017
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
public enum FrmInversionCuatrenioControladorEnum {

    PARAM("PARAM");

    private final String value;

    private FrmInversionCuatrenioControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
