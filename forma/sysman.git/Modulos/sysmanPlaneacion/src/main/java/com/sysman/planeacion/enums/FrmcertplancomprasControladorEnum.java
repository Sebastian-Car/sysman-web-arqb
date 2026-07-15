/*
 * FrmcertplancomprasControladorEnum
 *
 * 1.0
 *
 * 07/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmcertplancomprasControladorEnum {

    CODIGOINI("CODIGOINI"),

    INVENTARIO("INVENTARIO");

    private final String value;

    private FrmcertplancomprasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
