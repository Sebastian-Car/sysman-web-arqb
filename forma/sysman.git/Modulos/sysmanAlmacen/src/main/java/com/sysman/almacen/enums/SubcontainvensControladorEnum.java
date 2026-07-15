/*
 * SubcontainvensControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubcontainvensControladorEnum {

    PARAM3("PARAM3"),

    PARAM4("PARAM4"),

    PARAM1("PARAM1"),

    PARAM2("PARAM2"),

    PARAM0("TIPO"),

    PARAM9("PARAM9"),

    PARAM7("PARAM7"),

    PARAM8("PARAM8"),

    PARAM5("PARAM5"),

    PARAM10("PARAM10"),

    PARAM6("PARAM6"),

    PARAM11("PARAM11");

    private final String value;

    private SubcontainvensControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
