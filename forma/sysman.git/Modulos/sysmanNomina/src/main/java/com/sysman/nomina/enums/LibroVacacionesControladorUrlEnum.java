/*
 * LibroVacacionesControladorUrlEnum
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
public enum LibroVacacionesControladorUrlEnum {

    URL5195("LIBROVACACIONESCONTROLADORURL5195",
                    "471030"),

    URL6125("LIBROVACACIONESCONTROLADORURL6125",
                    "471026"),

    URL4426("LIBROVACACIONESCONTROLADORURL4426",
                    "471002");

    private final String key;
    private final String value;

    private LibroVacacionesControladorUrlEnum(String key, String value) {
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
