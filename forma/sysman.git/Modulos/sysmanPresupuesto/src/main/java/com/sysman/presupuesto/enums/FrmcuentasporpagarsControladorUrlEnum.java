/*
 * FrmcuentasporpagarsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmcuentasporpagarsControladorUrlEnum {

    URL5246("FRMCUENTASPORPAGARSCONTROLADORURL5246",
                    "94030"),

    URL3742("FRMCUENTASPORPAGARSCONTROLADORURL3742",
                    "4002"),

    URL5926("FRMCUENTASPORPAGARSCONTROLADORURL5926",
                    "94032"),

    URL4314("FRMCUENTASPORPAGARSCONTROLADORURL4314",
                    "7017");

    private final String key;
    private final String value;

    private FrmcuentasporpagarsControladorUrlEnum(String key, String value) {
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
