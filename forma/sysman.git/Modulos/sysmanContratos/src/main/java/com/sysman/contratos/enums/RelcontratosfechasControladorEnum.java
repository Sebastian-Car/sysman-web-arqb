/*
 * RelcontratosfechasControladorEnum
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
public enum RelcontratosfechasControladorEnum {

    PARAM1("NOMBREINICIAL"),

    PARAM2("CODIGOINICIAL"),

    PARAM0("TIPOCONTRATOINICIAL");

    private final String value;

    private RelcontratosfechasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
