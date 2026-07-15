/*
 * PedirciclonuevosControladorUrlEnum
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
public enum PedirciclonuevosControladorUrlEnum {

    URL7149("PEDIRCICLONUEVOSCONTROLADORURL7149",
                    "227049"),

    URL5853("PEDIRCICLONUEVOSCONTROLADORURL5853",
                    "254001"),

    URL5255("PEDIRCICLONUEVOSCONTROLADORURL5255",
                    "214085"),

    URL7361("PEDIRCICLONUEVOSCONTROLADORURL7361",
                    "256002"),

    URL1313("PEDIRCICLONUEVOSCONTROLADORURL1313",
                    "256001"),

    URL6969("PEDIRCICLONUEVOSCONTROLADORURL6969",
                    "227050");

    private final String key;
    private final String value;

    private PedirciclonuevosControladorUrlEnum(String key, String value) {
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
