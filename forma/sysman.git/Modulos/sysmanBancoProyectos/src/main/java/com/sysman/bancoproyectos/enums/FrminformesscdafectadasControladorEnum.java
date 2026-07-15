/*
 * FrminformesscdafectadasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrminformesscdafectadasControladorEnum {

    PARAM6("PARAM6"),

    PARAM5("PARAM5"),

    PARAM7("PARAM7"),

    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    CODIGOINICIAL("CODIGOINICIAL"),

    PROYECTOINICIAL("PROYECTOINICIAL");

    private final String value;

    private FrminformesscdafectadasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
