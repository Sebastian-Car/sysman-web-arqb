/*
 * EsrubrosestdisproysControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EsrubrosestdisproysControladorUrlEnum {

    URL7608("ESRUBROSESTDISPROYSCONTROLADORURL7608", "75001"),

    URL6771("ESRUBROSESTDISPROYSCONTROLADORURL6771", "25037"),

    URL7214("ESRUBROSESTDISPROYSCONTROLADORURL7214", "4001"),

    URL9053("ESRUBROSESTDISPROYSCONTROLADORURL9053", "38034");

    private final String key;
    private final String value;

    private EsrubrosestdisproysControladorUrlEnum(String key, String value) {
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
