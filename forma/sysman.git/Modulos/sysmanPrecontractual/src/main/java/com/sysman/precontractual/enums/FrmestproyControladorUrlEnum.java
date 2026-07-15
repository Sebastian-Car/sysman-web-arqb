/*
 * FrmestpreviounspscsControladorUrlEnum
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
public enum FrmestproyControladorUrlEnum {

    URL4029("FRMESTPREVIOUNSPSCSCONTROLADORURL4029", "511001"),

    URL4419("FRMESTPREVIOUNSPSCSCONTROLADORURL4419", "513001"),

    URL5592("FRMESTPREVIOUNSPSCSCONTROLADORURL5592", "503003"),

    URL5593("FRMESTPREVIOUNSPSCSCONTROLADORURL5593", "503005"),

    URL5594("FRMESTPREVIOUNSPSCSCONTROLADORURL5594", "503007");

    private final String key;
    private final String value;

    private FrmestproyControladorUrlEnum(String key, String value) {
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
