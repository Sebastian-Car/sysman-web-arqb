/*
 * FrmSubTransaccionesSstExternosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmSubTransaccionesSstExternosControladorUrlEnum {

    URL7435("FRMSUBTRANSACCIONESSSTEXTERNOSCONTROLADORURL7435",
                    "14156"),

    URL4444("FRMSUBTRANSACCIONESSSTEXTERNOSCONTROLADORURL4444",
                    "742001"),

    URL5555("FRMSUBTRANSACCIONESSSTEXTERNOSCONTROLADORURL5555",
                    "742003"),

    URL6666("FRMSUBTRANSACCIONESSSTEXTERNOSCONTROLADORURL6666",
                    "742004"),

    URL7777("FRMSUBTRANSACCIONESSSTEXTERNOSCONTROLADORURL7777",
                    "74200D");

    private final String key;
    private final String value;

    private FrmSubTransaccionesSstExternosControladorUrlEnum(String key,
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
