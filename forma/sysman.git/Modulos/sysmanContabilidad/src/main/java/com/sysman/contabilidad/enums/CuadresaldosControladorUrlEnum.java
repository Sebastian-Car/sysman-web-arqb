/*
 * CuadresaldosControladorUrlEnum
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
public enum CuadresaldosControladorUrlEnum {

    URL3216("CUADRESALDOSCONTROLADORURL3216", "16007"),

    URL3829("CUADRESALDOSCONTROLADORURL3829", "7015"),

    URL3491("CUADRESALDOSCONTROLADORURL3491", "7002");

    private final String key;
    private final String value;

    private CuadresaldosControladorUrlEnum(String key, String value) {
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
