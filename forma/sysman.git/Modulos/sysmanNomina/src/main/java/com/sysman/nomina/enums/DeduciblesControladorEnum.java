/*
 * DeduciblesControladorEnum
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
public enum DeduciblesControladorEnum {
    NOMBRE_CONCEPTO("NOMBRE_CONCEPTO"),

    ID_DE_PROCESO("ID_DE_PROCESO"),

    NOMBRECONCEPTO("NOMBRECONCEPTO"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    ID_DE_CONCEPTO("ID_DE_CONCEPTO");

    private final String value;

    private DeduciblesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
