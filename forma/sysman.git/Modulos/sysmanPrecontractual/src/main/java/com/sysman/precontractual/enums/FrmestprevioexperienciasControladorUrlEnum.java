/*
 * FrmestprevioexperienciasControladorUrlEnum
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
public enum FrmestprevioexperienciasControladorUrlEnum {

    URL6061("FRMESTPREVIOEXPERIENCIASCONTROLADORURL6061", "506001"),

    URL6500("FRMESTPREVIOEXPERIENCIASCONTROLADORURL6500", "506001"),

    URL001("FRMESTPREVIOEXPERIENCIASCONTROLADORURL001", "481003");

    private final String key;
    private final String value;

    private FrmestprevioexperienciasControladorUrlEnum(String key,
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
