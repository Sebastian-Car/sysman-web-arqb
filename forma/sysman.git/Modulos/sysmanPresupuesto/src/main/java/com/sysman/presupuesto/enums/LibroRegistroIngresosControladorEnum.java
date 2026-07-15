/*
 * LibroRegistroIngresosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum LibroRegistroIngresosControladorEnum {

    PARAM14("CODIGOINICIAL"),

    PARAM13("PARAM13"),

    PARAM9("CENTROINICIAL"),

    PARAM12("PARAM12"),

    PARAM11("PARAM11"),

    PARAM10("PARAM10"),

    PARAM6("PARAM6"),

    PARAM5("PARAM5"),

    PARAM8("PARAM8"),

    PARAM7("PARAM7"),

    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("CUENTAINICIAL"),

    PARAM0("PARAM0");

    private final String value;

    private LibroRegistroIngresosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
