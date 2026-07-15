/*
 * EmbargosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum EmbargosControladorEnum {

    TIPOEMBARGO("TIPOEMBARGO"),

    ID_EMPLEADO("ID_EMPLEADO"),

    PROCESO("PROCESO"),

    ANIO("ANIO"),

    MES("MES"),

    PERIODO("PERIODO"),

    DEMANDANTE("DEMANDANTE"),

    TIPO_DEMANDANTE("TIPO_DEMANDANTE"),

    ID_DEMANDANTE("ID_DEMANDANTE"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    NOMBRECONCEPTO("NOMBRECONCEPTO"),

    CUOTAS("CUOTAS"),

    NOMBRETIPO("NOMBRETIPO");

    private final String value;

    private EmbargosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
