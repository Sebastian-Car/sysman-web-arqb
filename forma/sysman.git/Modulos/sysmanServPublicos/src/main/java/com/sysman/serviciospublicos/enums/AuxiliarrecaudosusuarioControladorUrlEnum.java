/*
 * AuxiliarrecaudosusuarioControladorUrlEnum
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
public enum AuxiliarrecaudosusuarioControladorUrlEnum {

    URL7692("AUXILIARRECAUDOSUSUARIOCONTROLADORURL7692",
                    "345001"),

    URL6961("AUXILIARRECAUDOSUSUARIOCONTROLADORURL6961",
                    "345003"),

    URL6268("AUXILIARRECAUDOSUSUARIOCONTROLADORURL6268",
                    "214031");

    private final String key;
    private final String value;

    private AuxiliarrecaudosusuarioControladorUrlEnum(String key,
        String value) {
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
