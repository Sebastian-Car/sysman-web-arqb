/*
 * ObligaPedirDatosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum ObligaPedirDatosControladorEnum {

    ANO("ANIO"),

    CODEXCLUIDO("CODIGOEXCLUIDO"),

    CODCUENTA("CODCUENTA"),

    PORCENTAJE("PORCENTAJE"),

    PINI("PAGINICIO"),

    PTAMANO("PAGTAMANIO"),

    COSTOEXCLUIDO("CENTROCOSTOEXCLUIDO"),

    NOMBRE99("VARIOS"),

    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("PARAM0");

    private final String value;

    private ObligaPedirDatosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
