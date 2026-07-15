/*
 * AutoliquidacionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AutoliquidacionesControladorUrlEnum {

    URL19179("AUTOLIQUIDACIONESCONTROLADORURL19179", "630002"),

    URL15182("AUTOLIQUIDACIONESCONTROLADORURL15182", "642004"),

    URL10890("AUTOLIQUIDACIONESCONTROLADORURL10890", "620009"),

    URL6873("AUTOLIQUIDACIONESCONTROLADORURL6873", "620011"),

    URL9702("AUTOLIQUIDACIONESCONTROLADORURL9702", "630003"),

    URL7817("AUTOLIQUIDACIONESCONTROLADORURL7817", "7001"),

    URL7227("AUTOLIQUIDACIONESCONTROLADORURL7227", "4001"),

    URL518("AUTOLIQUIDACIONESCONTROLADORURL518", "630004");

    private final String key;
    private final String value;

    private AutoliquidacionesControladorUrlEnum(String key, String value) {
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
