/*
 * FrmobscobrosControladorUrlEnum
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
public enum FrmobscobrosControladorUrlEnum {

    URL001("FRMOBSCOBROSCONTROLADORURL001", "367101"),

    URL4758("FRMOBSCOBROSCONTROLADORURL4758", "367102");

    private final String key;
    private final String value;

    private FrmobscobrosControladorUrlEnum(String key, String value) {
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
