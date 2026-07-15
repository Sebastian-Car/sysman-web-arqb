/*
 * FrmmodificacionesdeudasControladorEnum
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
public enum FrmmodificacionesdeudasControladorEnum {

    PARAM3("PARAM3"),

    PARAM4("PARAM4"),

    PARAM1("TOTFACTURAPERACTUAL"),

    PARAM2("TOTFACTURAPAGO2"),

    PARAM0("HORA"),

    PARAM9("PARAM9"),

    PARAM12("PARAM12"),

    PARAM7("PARAM7"),

    PARAM13("PARAM13"),

    PARAM8("PARAM8"),

    PARAM5("PARAM5"),

    PARAM10("PARAM10"),

    PARAM11("PARAM11"),

    PARAM6("PARAM6");

    private final String value;

    private FrmmodificacionesdeudasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
