/*
 * FrmelegiblesControladorUrlEnum
 *
 * 1.0
 *
 * 30/01/2018
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
public enum FrmelegiblesControladorUrlEnum {

    URL4130("FRMELEGIBLESCONTROLADORURL4130", "708016"),

    URL0001("FRMELEGIBLESCONTROLADORURL0001", "708018")

    ;

    private final String key;
    private final String value;

    private FrmelegiblesControladorUrlEnum(String key, String value) {
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
