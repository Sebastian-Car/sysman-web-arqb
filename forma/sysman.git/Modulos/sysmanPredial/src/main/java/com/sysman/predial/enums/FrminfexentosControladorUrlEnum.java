/*
 * FrminfexentosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrminfexentosControladorUrlEnum {

    URL3877("FRMINFEXENTOSCONTROLADORURL3877",
                    "367086"),

    URL7697("FRMINFEXENTOSCONTROLADORURL7697",
                    "367090"),

    URL4776("FRMINFEXENTOSCONTROLADORURL4776",
                    "367088"),

    URL2892("FRMINFEXENTOSCONTROLADORURL2892",
                    "381004");

    private final String key;
    private final String value;

    private FrminfexentosControladorUrlEnum(String key, String value) {
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
