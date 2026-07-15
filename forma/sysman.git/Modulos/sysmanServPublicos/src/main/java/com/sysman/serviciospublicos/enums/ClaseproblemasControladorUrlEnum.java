/*
 * ClaseproblemasControladorUrlEnum
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
public enum ClaseproblemasControladorUrlEnum {

    URL4574("CLASEPROBLEMASCONTROLADORURL4574",
                    "258001"),

    URL3762("CLASEPROBLEMASCONTROLADORURL3762",
                    "234005"),

    URL6610("CLASEPROBLEMASCONTROLADORURL6610",
                    "326001");

    private final String key;
    private final String value;

    private ClaseproblemasControladorUrlEnum(String key, String value) {
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
