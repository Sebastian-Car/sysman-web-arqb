/*
 * NumerosdefacturaspControladorUrlEnum
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
public enum NumerosdefacturaspControladorUrlEnum {

    URL4375("NUMEROSDEFACTURASPCONTROLADORURL4375",
                    "231002"),

    URL4478("NUMEROSDEFACTURASPCONTROLADORURL4478",
                    "231003");

    private final String key;
    private final String value;

    private NumerosdefacturaspControladorUrlEnum(String key, String value) {
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
