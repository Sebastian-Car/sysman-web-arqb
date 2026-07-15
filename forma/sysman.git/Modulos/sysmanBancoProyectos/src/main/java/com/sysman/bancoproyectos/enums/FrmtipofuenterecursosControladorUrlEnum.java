/*
 * FrmtipofuenterecursosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmtipofuenterecursosControladorUrlEnum {

    URL2483("FRMTIPOFUENTERECURSOSCONTROLADORURL2483", "4001"),

    URL001("FRMTIPOFUENTERECURSOSCONTROLADORURL001", "34028"),

    URL002("FRMTIPOFUENTERECURSOSCONTROLADORURL002", "34029"),

    URL003("FRMTIPOFUENTERECURSOSCONTROLADORURL003", "34030"),

    URL004("FRMTIPOFUENTERECURSOSCONTROLADORURL004", "34033"),

    URL005("FRMTIPOFUENTERECURSOSCONTROLADORURL004", "34032"),

    URL12254("FRMTIPOFUENTERECURSOSCONTROLADORURL12254", "1025001");

    private final String key;
    private final String value;

    private FrmtipofuenterecursosControladorUrlEnum(String key, String value) {
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
