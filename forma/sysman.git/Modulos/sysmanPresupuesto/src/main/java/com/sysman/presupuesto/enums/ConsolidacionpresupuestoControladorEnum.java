/*
 * ConsolidacionpresupuestoControladorEnum
 *
 * 1.0
 *
 * 21/03/2018
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
public enum ConsolidacionpresupuestoControladorEnum {

    PERMITECONSOLIDAR("PERMITECONSOLIDAR"),

    PLAN_PRESUPUESTAL("PLAN_PRESUPUESTAL");

    private final String value;

    private ConsolidacionpresupuestoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
