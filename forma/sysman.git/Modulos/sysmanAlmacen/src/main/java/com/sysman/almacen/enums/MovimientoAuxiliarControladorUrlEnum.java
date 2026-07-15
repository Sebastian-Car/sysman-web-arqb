/*
 * CambiarNombreViaControladorUrlEnum
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
public enum MovimientoAuxiliarControladorUrlEnum {

    URL2842("MOVIMIENTOAUXILIARCONTROLADORURL2842", "23015"),

    URL2098("MOVIMIENTOAUXILIARCONTROLADORURL2098", "23015");

    private final String key;
    private final String value;

    private MovimientoAuxiliarControladorUrlEnum(String key, String value) {
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
