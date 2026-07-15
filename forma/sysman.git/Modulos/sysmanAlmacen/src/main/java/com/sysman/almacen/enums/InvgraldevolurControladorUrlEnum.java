/*
 * InvgraldevolurControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InvgraldevolurControladorUrlEnum {

    URL5185("INVGRALDEVOLURCONTROLADORURL5185",
                    "61012"),

    URL6258("INVGRALDEVOLURCONTROLADORURL6258",
                    "61014"),

    URL3302("INVGRALDEVOLURCONTROLADORURL3302",
                    "112044"),

    URL4170("INVGRALDEVOLURCONTROLADORURL4170",
                    "112046");

    private final String key;
    private final String value;

    private InvgraldevolurControladorUrlEnum(String key, String value) {
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
