/*
 * ListadoResIGACControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum ListadoResIGACControladorEnum {

    CODIGO_INICIAL("CODIGO_INICIAL"),

    NUMERO_ORDEN_PREDIAL("NUMERO_ORDEN_PREDIAL"),

    FECHAINGRESOSISTEMA("FECHAINGRESOSISTEMA");

    private final String value;

    private ListadoResIGACControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
