/*
 * InfResolucionesControladorEnum
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
public enum InfResolucionesControladorEnum {

    PARAM3("ID_PROCESO"),

    PARAM4("ANO"),

    PARAM1("IDPROCESO"),

    PARAM2("PROCESO"),

    PARAM0("MES"),

    PARAM5("FECHACONSULTA"),

    PARAM6("TIPO")

    ;

    private final String value;

    private InfResolucionesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
