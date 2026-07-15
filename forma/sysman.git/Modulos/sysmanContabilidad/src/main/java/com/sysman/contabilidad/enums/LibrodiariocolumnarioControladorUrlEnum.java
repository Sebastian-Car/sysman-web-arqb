/*
 * LibrodiariocolumnarioControladorUrlEnum
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
public enum LibrodiariocolumnarioControladorUrlEnum {

    URL3197("LIBRODIARIOCOLUMNARIOCONTROLADORURL3197", "15038"),

    URL4009("LIBRODIARIOCOLUMNARIOCONTROLADORURL4009", "15040");

    private final String key;
    private final String value;

    private LibrodiariocolumnarioControladorUrlEnum(String key, String value) {
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
