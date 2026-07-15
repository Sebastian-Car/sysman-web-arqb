/*
 * CalculoporlotesControladorUrlEnum
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
public enum CalculoporlotesControladorUrlEnum {

    URL5707("CALCULOPORLOTESCONTROLADORURL5707", "367009"),

    URL6542("CALCULOPORLOTESCONTROLADORURL6542", "367011"),

    URL7506("CALCULOPORLOTESCONTROLADORURL7506", "367013"),

    URL8844("CALCULOPORLOTESCONTROLADORURL8844", "367015"),

    URL10361("CALCULOPORLOTESCONTROLADORURL10361", "367017"),

    URL11332("CALCULOPORLOTESCONTROLADORURL11332", "367019");

    private final String key;
    private final String value;

    private CalculoporlotesControladorUrlEnum(String key, String value) {
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
