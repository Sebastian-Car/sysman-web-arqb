/*
 * LisresultadosCGRControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LisresultadosCGRControladorUrlEnum {

    URL5057("LISRESULTADOSCGRCONTROLADORURL5057",
                    "4001"),

    URL5909("LISRESULTADOSCGRCONTROLADORURL5909",
                    "16067"),

    URL7051("LISRESULTADOSCGRCONTROLADORURL7051",
                    "16071"),

    URL5540("LISRESULTADOSCGRCONTROLADORURL5540",
                    "7011");

    private final String key;
    private final String value;

    private LisresultadosCGRControladorUrlEnum(String key, String value) {
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
