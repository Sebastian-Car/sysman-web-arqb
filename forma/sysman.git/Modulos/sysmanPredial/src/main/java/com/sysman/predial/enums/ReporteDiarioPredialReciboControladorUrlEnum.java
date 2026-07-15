/*
 * ReporteDiarioPredialReciboControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ReporteDiarioPredialReciboControladorUrlEnum {

    URL6580("REPORTEDIARIOPREDIALRECIBOCONTROLADORURL6580", "408009"),

    URL6581("REPORTEDIARIOPREDIALRECIBOCONTROLADORURL6581", "408010"),

    URL6582("REPORTEDIARIOPREDIALRECIBOCONTROLADORURL6582", "375004"),

    URL6583("REPORTEDIARIOPREDIALRECIBOCONTROLADORURL6583", "375006");

    private final String key;
    private final String value;

    private ReporteDiarioPredialReciboControladorUrlEnum(String key,
        String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
