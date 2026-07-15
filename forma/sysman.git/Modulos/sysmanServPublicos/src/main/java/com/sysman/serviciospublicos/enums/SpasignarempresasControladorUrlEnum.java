/*
 * SpasignarempresasControladorUrlEnum
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
public enum SpasignarempresasControladorUrlEnum {

    URL0001("SPASIGNAREMPRESASCONTROLADORURL0001", "213212"),

    URL0002("SPASIGNAREMPRESASCONTROLADORURL0002", "213214"),

    URL5683("SPASIGNAREMPRESASCONTROLADORURL5683", "319003");

    private final String key;
    private final String value;

    private SpasignarempresasControladorUrlEnum(String key, String value) {
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
