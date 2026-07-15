/*
 * ModificacionesperiodoconceptoControladorUrlEnum
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
public enum ModificacionesperiodoconceptoControladorUrlEnum {

    URL7278("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL7278",
                    "227002"),

    URL8937("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL8937",
                    "213059"),

    URL8408("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL8408",
                    "227002"),

    URL9666("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL9666",
                    "213061"),

    URL7842("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL7842",
                    "227003"),

    URL6806("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL6806",
                    "227001"),

    URL6191("MODIFICACIONESPERIODOCONCEPTOCONTROLADORURL6191",
                    "214031");

    private final String key;
    private final String value;

    private ModificacionesperiodoconceptoControladorUrlEnum(String key,
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
