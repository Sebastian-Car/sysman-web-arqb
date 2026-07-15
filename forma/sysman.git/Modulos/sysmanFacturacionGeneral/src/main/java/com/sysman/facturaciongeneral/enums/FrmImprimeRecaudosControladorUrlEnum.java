/*
 * FrmImprimeRecaudosControladorUrlEnum
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
public enum FrmImprimeRecaudosControladorUrlEnum {

    URL7342("FRMIMPRIMERECAUDOSCONTROLADORURL7342", "72071"),

    URL6330("FRMIMPRIMERECAUDOSCONTROLADORURL6330", "72069"),

    URL5656("FRMIMPRIMERECAUDOSCONTROLADORURL5656", "72068"),

    URL8413("FRMIMPRIMERECAUDOSCONTROLADORURL8413", "72073");

    private final String key;
    private final String value;

    private FrmImprimeRecaudosControladorUrlEnum(String key, String value) {
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
