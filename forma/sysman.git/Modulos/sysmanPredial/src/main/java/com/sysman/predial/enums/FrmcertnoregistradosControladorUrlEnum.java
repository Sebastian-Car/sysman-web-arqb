/*
 * FrmcertnoregistradosControladorUrlEnum
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
public enum FrmcertnoregistradosControladorUrlEnum {

    URL5730("FRMCERTNOREGISTRADOSCONTROLADORURL5730", "393005"),

    URL9425("FRMCERTNOREGISTRADOSCONTROLADORURL9425", "104032"),

    URL18797("FRMCERTNOREGISTRADOSCONTROLADORURL18797", "367057"),

    URL6316("FRMCERTNOREGISTRADOSCONTROLADORURL6316", "394001"),

    URL8489("FRMCERTNOREGISTRADOSCONTROLADORURL8489", "2002"),

    URL001("FRMCERTNOREGISTRADOSCONTROLADORURL001", "400006");

    private final String key;
    private final String value;

    private FrmcertnoregistradosControladorUrlEnum(String key, String value) {
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
