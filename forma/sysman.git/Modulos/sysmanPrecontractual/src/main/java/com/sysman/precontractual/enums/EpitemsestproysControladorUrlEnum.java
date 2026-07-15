/*
 * EpitemsestproysControladorUrlEnum
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
public enum EpitemsestproysControladorUrlEnum {

    URL19449("EPITEMSESTPROYSCONTROLADORURL19449", ""),

    URL15413("EPITEMSESTPROYSCONTROLADORURL15413", ""),

    URL14511("EPITEMSESTPROYSCONTROLADORURL14511", "112090"),

    URL17702("EPITEMSESTPROYSCONTROLADORURL17702", ""),

    URL16321("EPITEMSESTPROYSCONTROLADORURL16321", "62040"),

    URL17206("EPITEMSESTPROYSCONTROLADORURL17206", "71008"),

    URL25393("EPITEMSESTPROYSCONTROLADORURL25393", "477001"),

    URL13402("EPITEMSESTPROYSCONTROLADORURL13402", "");

    private final String key;
    private final String value;

    private EpitemsestproysControladorUrlEnum(String key, String value) {
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
