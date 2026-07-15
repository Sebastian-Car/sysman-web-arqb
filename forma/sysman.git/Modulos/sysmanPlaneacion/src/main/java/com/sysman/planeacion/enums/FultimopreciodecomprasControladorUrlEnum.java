/*
 * FultimopreciodecomprasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FultimopreciodecomprasControladorUrlEnum {

    URL3221("FULTIMOPRECIODECOMPRASCONTROLADORURL3221",
                    "113003"),

    URL4666("FULTIMOPRECIODECOMPRASCONTROLADORURL4666",
                    "113005");

    private final String key;
    private final String value;

    private FultimopreciodecomprasControladorUrlEnum(String key, String value) {
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
