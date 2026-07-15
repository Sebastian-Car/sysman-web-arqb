/*
 * InventarioMinimaMaximaControladorUrlEnum
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
public enum InventarioMinimaMaximaControladorUrlEnum {

    URL3733("INVENTARIOMINIMAMAXIMACONTROLADORURL3733",
                    "112032"),

    URL4513("INVENTARIOMINIMAMAXIMACONTROLADORURL4513",
                    "112034");

    private final String key;
    private final String value;

    private InventarioMinimaMaximaControladorUrlEnum(String key, String value) {
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
