/*
 * RecaudosAbonosControladorUrlEnum
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
public enum RecaudosAbonosControladorUrlEnum {

    URL6347("RECAUDOSABONOSCONTROLADORURL6347", "214007"),

    URL6852("RECAUDOSABONOSCONTROLADORURL6852", "214027"),

    URL7434("RECAUDOSABONOSCONTROLADORURL7434", "227002"),

    URL8182("RECAUDOSABONOSCONTROLADORURL8182", "227034");

    private final String key;
    private final String value;

    private RecaudosAbonosControladorUrlEnum(String key, String value) {
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
