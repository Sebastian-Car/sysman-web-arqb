/*
 * LFacturacionControladorEnum
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
public enum LFacturacionControladorEnum {
    CODIGO_BARRIO("CODIGO_BARRIO"),

    NUMERO_NUEVE("9999999999999999"),

    CODIGORUTA("CODIGORUTA"),

    NOMBREPERIODO("NOMBREPERIODO"),

    TODOS("TODOS"),

    USO("USO"),

    ESTRATOINI("ESTRATOINI"),

    NIT("NIT"),

    ESTRATOFIN("ESTRATOFIN"),

    USOFIN("USOFIN"),

    USOINICIAL("USOINICIAL"),

    CODIGOFINAL("CODIGOFINAL"),

    CODIGOINICIAL("CODIGOINICIAL"),

    USO_ACTUAL("USO_ACTUAL");

    private final String value;

    private LFacturacionControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
