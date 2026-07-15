/*
 * ListadoUsuarioFactSitioControladorUrlEnum
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
public enum ListadoUsuarioFactSitioControladorUrlEnum {

    URL214030("LISTADOUSUARIOFACTSITIOCONTROLADORURL214030", "214030"),

    URL5338("LISTADOUSUARIOFACTSITIOCONTROLADORURL5338", "214020"),

    URL4922("LISTADOUSUARIOFACTSITIOCONTROLADORURL4922", "118005");

    private final String key;
    private final String value;

    private ListadoUsuarioFactSitioControladorUrlEnum(String key,
        String value) {
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
