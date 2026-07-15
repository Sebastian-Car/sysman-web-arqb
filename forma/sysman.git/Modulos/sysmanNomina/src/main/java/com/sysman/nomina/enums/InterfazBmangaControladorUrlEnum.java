/*
 * InterfazBmangaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InterfazBmangaControladorUrlEnum {

    URL5689("INTERFAZBMANGACONTROLADORURL5689",
                    "471025"),

    URL4865("INTERFAZBMANGACONTROLADORURL4865",
                    "4001"),

    URL6219("INTERFAZBMANGACONTROLADORURL6219",
                    "15009"),

    URL5255("INTERFAZBMANGACONTROLADORURL5255",
                    "7001");

    private final String key;
    private final String value;

    private InterfazBmangaControladorUrlEnum(String key, String value) {
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
