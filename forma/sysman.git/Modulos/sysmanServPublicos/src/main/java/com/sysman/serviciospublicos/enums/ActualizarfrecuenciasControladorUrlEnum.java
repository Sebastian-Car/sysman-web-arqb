/*
 * ActualizarfrecuenciasControladorUrlEnum
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
public enum ActualizarfrecuenciasControladorUrlEnum {

    URL3303("ACTUALIZARFRECUENCIASCONTROLADORURL3303", "213004"),

    URL3937("ACTUALIZARFRECUENCIASCONTROLADORURL3937", "213006"),

    URL3003("ACTUALIZARFRECUENCIASCONTROLADORURL3003", "214005");

    private final String key;
    private final String value;

    private ActualizarfrecuenciasControladorUrlEnum(String key, String value) {
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
