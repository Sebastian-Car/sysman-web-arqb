/*
 * FrmCalendarioControladorUrlEnum
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
public enum FrmCalendarioControladorUrlEnum {

    URL10901("FRMCALENDARIOCONTROLADORURL10901", "141028"),

    URL122("FRMCALENDARIOCONTROLADORURL122", "4001"),

    URL4476("FRMCALENDARIOCONTROLADORURL4476", "141023"),

    URL8890("FRMCALENDARIOCONTROLADORURL8890", "96001"),

    URL406("FRMCALENDARIOCONTROLADORURL406", "147001");

    private final String key;
    private final String value;

    private FrmCalendarioControladorUrlEnum(String key, String value) {
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
