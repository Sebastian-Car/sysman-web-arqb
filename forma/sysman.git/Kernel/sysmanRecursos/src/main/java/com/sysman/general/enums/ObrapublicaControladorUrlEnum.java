/*
 * NnovedadesacontratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ObrapublicaControladorUrlEnum {

    URL0001("NNOVEDADESACONTRATOSCONTROLADORURL0001", "82078"),

    URL0002("NNOVEDADESACONTRATOSCONTROLADORURL0002", "82079");

    private final String key;
    private final String value;

    private ObrapublicaControladorUrlEnum(String key, String value) {
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
