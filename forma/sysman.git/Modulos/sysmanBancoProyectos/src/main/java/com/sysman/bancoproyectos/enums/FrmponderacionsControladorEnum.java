/*
 * FrmponderacionsControladorEnum
 *
 * 1.0
 *
 * 21/09/2017
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
public enum FrmponderacionsControladorEnum {

    META_PRODUC("META_PRODUC"),

    ID("ID"),

    BP_PLAN_INDICATIVO("BP_PLAN_INDICATIVO"),

    KEY_ID("KEY_ID");

    private final String value;

    private FrmponderacionsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
