/*
 * IadyprocontratoControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum IadyprocontratoControladorEnum {

    PARAM5("NUMERO_INICIAL"),

    PARAM2("TIPOCONTRATOINICIAL"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("FIRMA ORDENES DE SERVICIO");

    private final String value;

    private IadyprocontratoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
