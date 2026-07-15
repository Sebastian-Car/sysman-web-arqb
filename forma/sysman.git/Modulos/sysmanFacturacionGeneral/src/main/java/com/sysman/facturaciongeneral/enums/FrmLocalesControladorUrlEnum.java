/*
 * FrmLocalesControladorUrlEnum
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
public enum FrmLocalesControladorUrlEnum {

    URL4748("FRMLOCALESCONTROLADORURL4748",
                    "1837001"),

    URL5073("FRMLOCALESCONTROLADORURL5073",
                    "1838001"),

    URL5431("FRMLOCALESCONTROLADORURL5431",
                    "1838001"),

    URL4431("FRMLOCALESCONTROLADORURL4431",
                    "1837001");

    private final String key;
    private final String value;

    private FrmLocalesControladorUrlEnum(String key, String value) {
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
