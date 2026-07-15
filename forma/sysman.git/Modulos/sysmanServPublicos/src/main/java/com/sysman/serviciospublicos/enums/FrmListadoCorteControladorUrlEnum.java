/*
 * FrmListadoCorteControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmListadoCorteControladorUrlEnum {

    URL7671("FRMLISTADOCORTECONTROLADORURL7671", "366004"),

    URL8124("FRMLISTADOCORTECONTROLADORURL8124", "214053"),

    URL6823("FRMLISTADOCORTECONTROLADORURL6823", "366006");

    private final String key;
    private final String value;

    private FrmListadoCorteControladorUrlEnum(String key, String value) {
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
