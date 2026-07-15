/*
 * ComprobantecntretencionsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracionn que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum ComprobantecntretencionsControladorEnum {

    PARAM2("TIPO"),

    PARAM1("ANO"),

    PARAM0("COMPANIA");

    private final String value;

    private ComprobantecntretencionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
