/*
 * FrmadicionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmadicionControladorUrlEnum {

    URL5727("FRMADICIONCONTROLADORURL5727", "136016"),

    URL7072("FRMADICIONCONTROLADORURL7072", "61012"),

    URL7073("FRMADICIONCONTROLADORURL7073", "61010"),

    URL3981("FRMADICIONCONTROLADORURL3981", "137016"),

    URL7074("FRMADICIONCONTROLADORURL7074", "153001"),

    URL7933("FRMADICIONCONTROLADORURL7933", "119009");

    private final String key;
    private final String value;

    private FrmadicionControladorUrlEnum(String key, String value) {
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
