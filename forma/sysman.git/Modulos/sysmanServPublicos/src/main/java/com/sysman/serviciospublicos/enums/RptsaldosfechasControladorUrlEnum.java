/*
 * RptsaldosfechasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RptsaldosfechasControladorUrlEnum {

    URL9549("RPTSALDOSFECHASCONTROLADORURL9549",
                    "214031"),

    URL10879("RPTSALDOSFECHASCONTROLADORURL10879",
                    "345001"),

    URL10192("RPTSALDOSFECHASCONTROLADORURL10192",
                    "345003");

    private final String key;
    private final String value;

    private RptsaldosfechasControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
