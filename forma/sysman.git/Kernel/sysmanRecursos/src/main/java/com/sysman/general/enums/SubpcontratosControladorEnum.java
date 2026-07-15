/*
 * SubpcontratosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubpcontratosControladorEnum {

    PARAM0("TIPOREGISTRO"),

    PARAM1("TIPOCPTE"),

    PARAM2("NUMPPTO"),

    PARAM3("NUMEROORDEN"),

    PARAM4("NUEVACANTIDAD"),

    PARAM5("VACIA"),

    PARAM6("VLRIVA"),

    PARAM7("VLRDESCUENTO"),

    PARAM8("VLRTOTAL"),

    PARAM9("SALDOCANT"),

    PARAM10("CUATROXMIL"),

    PARAM11("NUMEROCOM");

    private final String value;

    private SubpcontratosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
