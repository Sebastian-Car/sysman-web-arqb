/*
 * NnovedadesacontratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NnovedadesacontratosControladorUrlEnum {

    URL3116("NNOVEDADESACONTRATOSCONTROLADORURL3116", "73010"),

    URL3887("NNOVEDADESACONTRATOSCONTROLADORURL3887", "73029");

    private final String key;
    private final String value;

    private NnovedadesacontratosControladorUrlEnum(String key, String value) {
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
