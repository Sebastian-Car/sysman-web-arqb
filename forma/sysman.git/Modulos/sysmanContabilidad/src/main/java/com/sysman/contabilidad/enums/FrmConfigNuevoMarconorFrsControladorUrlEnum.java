/*
 * ComprobanteDiferidoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmConfigNuevoMarconorFrsControladorUrlEnum {

    URL7452("FRMCONFIGNUEVOMARCONORFRSCONTROLADORURL7452", "16193"),

    URL9578("FRMCONFIGNUEVOMARCONORFRSCONTROLADORURL9578", "16191"),

    URL5645("FRMCONFIGNUEVOMARCONORFRSCONTROLADORURL5645", "16195");

    private final String key;
    private final String value;

    private FrmConfigNuevoMarconorFrsControladorUrlEnum(String key,
        String value) {
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
