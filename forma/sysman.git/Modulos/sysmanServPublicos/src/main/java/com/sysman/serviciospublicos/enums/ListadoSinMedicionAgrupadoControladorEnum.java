/*
 * ListadoSinMedicionAgrupadoControladorEnum
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
public enum ListadoSinMedicionAgrupadoControladorEnum {

    PARAM3("PARAM3"),

    PARAM1("CODIGO_INICIAL"),

    PARAM2("PARAM2"),

    PARAM0("PROBLEMAINICIAL");

    private final String value;

    private ListadoSinMedicionAgrupadoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
