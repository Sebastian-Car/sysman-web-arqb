/*
 * DesviacionesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum DesviacionesControladorEnum {

    PARAM8("NOMBREPLANTILLA"),

    PARAM7("FECHAGENERACION"),

    PARAM6("TIPO"),

    PARAM5("PERIODOCIERRE"),

    PARAM2("31"),

    PARAM1("SUBCLASE"),

    PARAM4("DESVIACION"),

    PARAM3("PREGUNTA"),

    PARAM0("DESVIACIONCARTA");

    private final String value;

    private DesviacionesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
