/*
 * FrmcuotasacuerdosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmcuotasacuerdosControladorEnum {

    PARAM4("CODIGOINICIAL"),

    PARAM3("ACUERDOINICIAL"),

    PARAM2("CODIGOACUERDO"),

    PARAM1("BANCOINICIAL"),

    PARAM0("CODIGOBANCO");

    private final String value;

    private FrmcuotasacuerdosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
