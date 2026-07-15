/*
 * LsubsobrelisControladorUrlEnum
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
public enum LsubsobrelisControladorUrlEnum {

    URL13098("LSUBSOBRELISCONTROLADORURL13098",
                    "214020"),

    URL13761("LSUBSOBRELISCONTROLADORURL13761",
                    "214024"),

    URL6969("LSUBSOBRELISCONTROLADORURL6969",
                    "213163"),

    URL6666("LSUBSOBRELISCONTROLADORURL6666",
                    "213164"),

    URL4164("LSUBSOBRELISCONTROLADORURL4164",
                    "242005"),

    URL002("LSUBSOBRELISCONTROLADORURL002",
                    "213206"),

    URL003("LSUBSOBRELISCONTROLADORURL003",
                    "213208");

    private final String key;
    private final String value;

    private LsubsobrelisControladorUrlEnum(String key, String value) {
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
