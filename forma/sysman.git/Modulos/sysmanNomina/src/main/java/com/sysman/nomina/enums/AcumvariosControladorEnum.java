/*
 * AcumvariosControladorEnum
 *
 * 1.0
 *
 * 05/09/2017
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
public enum AcumvariosControladorEnum {

    EMPLEADO("EMPLEADO"),

    ID_DE_CONCEPTO("ID_DE_CONCEPTO"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    NOMBRE_CONCEPTO("NOMBRE_CONCEPTO"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),
    
    NOMCOMPLETO("NOMCOMPLETO");

    private final String value;

    private AcumvariosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
