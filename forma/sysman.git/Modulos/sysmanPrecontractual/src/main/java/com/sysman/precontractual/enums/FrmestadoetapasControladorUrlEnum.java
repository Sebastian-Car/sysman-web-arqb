/*
 * FrmestadoetapasControladorUrlEnum
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
public enum FrmestadoetapasControladorUrlEnum {

    URL4560("FRMESTADOETAPASCONTROLADORURL4560", "497001"),

    URL5796("FRMESTADOETAPASCONTROLADORURL5796", "188007"),

    URL7109("FRMESTADOETAPASCONTROLADORURL7109", "497003"),

    URL3465("FRMESTADOETAPASCONTROLADORURL3465", "188005"),

    URL2867("FRMESTADOETAPASCONTROLADORURL2867", "184003");

    private final String key;
    private final String value;

    private FrmestadoetapasControladorUrlEnum(String key, String value) {
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
