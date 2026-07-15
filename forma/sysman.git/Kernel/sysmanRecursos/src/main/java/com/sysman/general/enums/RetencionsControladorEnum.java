/*
 * RetencionsControladorEnum
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
public enum RetencionsControladorEnum {

    PARAM4("TIPON_LB"),

    PARAM3("CREATEDBY"),

    PARAM2("DATECREATED"),

    PARAM1("ANOORIGEN"),

    PARAM0("ANODES");

    private final String value;

    private RetencionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
