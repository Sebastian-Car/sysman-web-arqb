/*
 * LismovimientosControladorUrlEnum
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
public enum LismovimientosControladorUrlEnum {

    URL4421("LISMOVIMIENTOSCONTROLADORURL4421", "15003"),

    URL7291("LISMOVIMIENTOSCONTROLADORURL7291", "29029"),

    URL9258("LISMOVIMIENTOSCONTROLADORURL9258", "20013"),

    URL3606("LISMOVIMIENTOSCONTROLADORURL3606", "15005"),

    URL10167("LISMOVIMIENTOSCONTROLADORURL10167", "20015"),

    URL5487("LISMOVIMIENTOSCONTROLADORURL5487", "29027");

    private final String key;
    private final String value;

    private LismovimientosControladorUrlEnum(String key, String value) {
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
