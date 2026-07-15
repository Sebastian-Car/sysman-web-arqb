/*
 * FrmindicaexentosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmindicaexentosControladorUrlEnum {

    URL4147("FRMINDICAEXENTOSCONTROLADORURL4147", "4002"),

    URL0003("FRMINDICAEXENTOSCONTROLADORURL0003", "4024"),

    URL5121("FRMINDICAEXENTOSCONTROLADORURL5121", "367078");

    private final String key;
    private final String value;

    private FrmindicaexentosControladorUrlEnum(String key, String value) {
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
