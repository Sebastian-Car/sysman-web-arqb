/*
 * OrdeninventarioinicialsControladorEnum
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
public enum OrdeninventarioinicialsControladorEnum {

    PARAM0("TIPO"),

    PARAM1("ORDENDECOMPRA"),

    PARAM2("DIFERENCIA"),

    PARAM3("ORIGEN"),

    PARAM4("CONS_TERCERO"),

    PARAM5("CONS_SUCURSAL");

    private final String value;

    private OrdeninventarioinicialsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
