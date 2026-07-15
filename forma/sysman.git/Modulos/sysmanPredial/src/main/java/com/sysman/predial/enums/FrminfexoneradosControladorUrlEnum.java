/*
 * FrminfexoneradosControladorUrlEnum
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
public enum FrminfexoneradosControladorUrlEnum {

    URL3811("FRMINFEXONERADOSCONTROLADORURL3811", "367051"),

    URL4710("FRMINFEXONERADOSCONTROLADORURL4710", "367053");

    private final String key;
    private final String value;

    private FrminfexoneradosControladorUrlEnum(String key, String value) {
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
