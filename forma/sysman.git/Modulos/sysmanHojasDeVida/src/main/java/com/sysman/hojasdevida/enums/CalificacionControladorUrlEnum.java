/*
 * CalificacionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CalificacionControladorUrlEnum {

    URL5181("CALIFICACIONCONTROLADORURL5181",
                    "708007"),

    URL5166("CALIFICACIONCONTROLADORURL5166",
                    "708011"),

    URL6227("CALIFICACIONCONTROLADORURL6227",
                    "689003"),

    URL6196("CALIFICACIONCONTROLADORURL6227",
                    "708013")

    ;

    private final String key;
    private final String value;

    private CalificacionControladorUrlEnum(String key, String value) {
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
