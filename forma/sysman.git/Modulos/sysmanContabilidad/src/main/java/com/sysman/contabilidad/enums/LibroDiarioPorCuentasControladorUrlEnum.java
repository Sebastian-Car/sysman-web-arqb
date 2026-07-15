/*
 * LibroDiarioPorCuentasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LibroDiarioPorCuentasControladorUrlEnum {

    URL5829("LIBRODIARIOPORCUENTASCONTROLADORURL5829", "29029"),

    URL3963("LIBRODIARIOPORCUENTASCONTROLADORURL3963", "15003"),

    URL4797("LIBRODIARIOPORCUENTASCONTROLADORURL4797", "29027"),

    URL3284("LIBRODIARIOPORCUENTASCONTROLADORURL3284", "15005");

    private final String key;
    private final String value;

    private LibroDiarioPorCuentasControladorUrlEnum(String key, String value) {
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
