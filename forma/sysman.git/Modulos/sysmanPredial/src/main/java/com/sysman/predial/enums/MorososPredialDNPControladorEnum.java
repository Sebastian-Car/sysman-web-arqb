/*
 * InformeIngresosCarUnificadoControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum MorososPredialDNPControladorEnum {

    PARAM2("#$parametro#$"),

    PARAM1("TB_TB283"),

    PARAM0("MSM_TRANS_INTERRUMPIDA");

    private final String value;

    private MorososPredialDNPControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
