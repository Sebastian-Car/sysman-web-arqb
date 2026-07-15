/*
 * ModificacionesdeudaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ModificacionesdeudaControladorUrlEnum {

    URL9576("MODIFICACIONESDEUDACONTROLADORURL9576", "213004"),

    URL10444("MODIFICACIONESDEUDACONTROLADORURL10444", "213006"),

    URL6973("MODIFICACIONESDEUDACONTROLADORURL6973", "214029"),

    URL8036("MODIFICACIONESDEUDACONTROLADORURL8036", "227002"),

    URL8600("MODIFICACIONESDEUDACONTROLADORURL8600", "227021"),

    URL7583("MODIFICACIONESDEUDACONTROLADORURL7583", "227045"),

    URL0001("MODIFICACIONESDEUDACONTROLADORURL0001", "227046");

    private final String key;
    private final String value;

    private ModificacionesdeudaControladorUrlEnum(String key, String value) {
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
