/*
 * DetallePorConceptoControladorEnum
 *
 * 1.0
 *
 * 06/09/2017
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
public enum DetallePorConceptoControladorEnum {

    ID_DE_TIPO("ID_DE_TIPO"),

    NOMBRE_TIPO("NOMBRE_TIPO"),

    ID_DE_CONCEPTO("ID_DE_CONCEPTO"),

    NOMBRE_CONCEPTO("NOMBRE_CONCEPTO");

    private final String value;

    private DetallePorConceptoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
