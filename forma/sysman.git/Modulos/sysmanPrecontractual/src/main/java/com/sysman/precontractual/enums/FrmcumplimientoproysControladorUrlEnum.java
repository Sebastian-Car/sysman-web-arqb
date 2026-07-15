/*
 * FrmcumplimientoproysControladorUrlEnum
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
public enum FrmcumplimientoproysControladorUrlEnum {

    URL10589("FRMCUMPLIMIENTOPROYSCONTROLADORURL10589",
                    "61012"),

    URL8734("FRMCUMPLIMIENTOPROYSCONTROLADORURL8734",
                    "473001"),

    URL7407("FRMCUMPLIMIENTOPROYSCONTROLADORURL7407",
                    "472001"),

    URL12285("FRMCUMPLIMIENTOPROYSCONTROLADORURL12285",
                    "61012"),

    URL001("FRMCUMPLIMIENTOPROYSCONTROLADORURL001",
                    "493001"),

    URL002("FRMCUMPLIMIENTOPROYSCONTROLADORURL002",
                    "49300C"),

    URL003("FRMCUMPLIMIENTOPROYSCONTROLADORURL003",
                    "49300U"),

    URL004("FRMCUMPLIMIENTOPROYSCONTROLADORURL004",
                    "49300D");

    private final String key;
    private final String value;

    private FrmcumplimientoproysControladorUrlEnum(String key, String value) {
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
