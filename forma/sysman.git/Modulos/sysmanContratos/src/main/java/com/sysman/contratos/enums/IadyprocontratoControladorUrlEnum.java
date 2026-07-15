/*
 * IadyprocontratoControladorUrlEnum
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
public enum IadyprocontratoControladorUrlEnum {

    URL2957("IADYPROCONTRATOCONTROLADORURL2957", "73010"),

    URL4650("IADYPROCONTRATOCONTROLADORURL4650", "73026"),

    URL3746("IADYPROCONTRATOCONTROLADORURL3746", "73024");

    private final String key;
    private final String value;

    private IadyprocontratoControladorUrlEnum(String key, String value) {
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
