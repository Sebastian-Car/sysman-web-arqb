/*
 * WsradicacioncontratosContraladorEnum
 *
 * 1.0
 *
 * 15/08/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum WsradicacioncontratosContraladorEnum {

    CODIGOINI("CODIGOINI");

    private final String value;

    private WsradicacioncontratosContraladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
