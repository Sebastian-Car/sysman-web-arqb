/*
 * FrmestproypastosControladorUrlEnum
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
public enum FrmestproypastosControladorUrlEnum {

    URL5784("FRMESTPROYPASTOSCONTROLADORURL5784", "130007"),

    URL7155("FRMESTPROYPASTOSCONTROLADORURL7155", "131002"),

    URL0001("FRMESTPROYPASTOSCONTROLADORURL0001", "203002"),

    URL5487("FRMESTPROYPASTOSCONTROLADORURL5487", "509001"),

    URL4328("FRMESTPROYPASTOSCONTROLADORURL4328", "509002"),

    URL6470("FRMESTPROYPASTOSCONTROLADORURL6470", "50900D");

    private final String key;
    private final String value;

    private FrmestproypastosControladorUrlEnum(String key, String value) {
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
