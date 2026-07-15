/*
 * VidaUtilPlacasControladorUrlEnum
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

public enum MovimientosInventarioInicialControladorUrlEnum {

    URL0001("VIDAUTILPLACASCONTROLADORURL0001", "141090"),
    
    URL1984001("PARAMETROSALMACENCONTROLADORURL", "1984001"),
    
    URL1984002("PARAMETROSALMACENCONTROLADORURL", "1984002");

    private final String key;
    private final String value;

    private MovimientosInventarioInicialControladorUrlEnum(String key, String value) {
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
