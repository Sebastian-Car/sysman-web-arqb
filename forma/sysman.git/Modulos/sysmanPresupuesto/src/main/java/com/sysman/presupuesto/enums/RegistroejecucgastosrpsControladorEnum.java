/*
 * RegistroejecucgastosrpsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum RegistroejecucgastosrpsControladorEnum {

    PARAM2("CODIGOINICIAL"),

    PARAM1("ANO"),

    PARAM0("COMPANIA");

    private final String value;

    private RegistroejecucgastosrpsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
