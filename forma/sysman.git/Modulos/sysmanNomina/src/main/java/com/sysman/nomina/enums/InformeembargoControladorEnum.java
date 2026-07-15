/*
 * InformeembargoControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum InformeembargoControladorEnum {

    PARAM3("ID_DE_PROCESO"),

    PARAM4("ANO"),

    PARAM1("PROCESO"),

    PARAM2("MES");

    private final String value;

    private InformeembargoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
