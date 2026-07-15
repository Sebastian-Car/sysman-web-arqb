/*
 * PlanillaPrimaDiciembreControladorEnum
 *
 * 1.0
 *
 * 19/10/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum PlanillaPrimaDiciembreControladorEnum {
    PARAM0("PARAM0");

    private final String value;

    private PlanillaPrimaDiciembreControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
