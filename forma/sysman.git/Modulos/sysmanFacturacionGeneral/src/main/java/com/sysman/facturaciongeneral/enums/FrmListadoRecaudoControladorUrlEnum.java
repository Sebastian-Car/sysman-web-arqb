/*
 * FrmListadoRecaudoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmListadoRecaudoControladorUrlEnum {

    URL7916("FRMLISTADORECAUDOCONTROLADORURL7916", "663007"),

    URL8718("FRMLISTADORECAUDOCONTROLADORURL8718", "29123"),

    URL9802("FRMLISTADORECAUDOCONTROLADORURL9802", "29125"),

    URL7176("FRMLISTADORECAUDOCONTROLADORURL7176", "663005");

    private final String key;
    private final String value;

    private FrmListadoRecaudoControladorUrlEnum(String key, String value) {
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
