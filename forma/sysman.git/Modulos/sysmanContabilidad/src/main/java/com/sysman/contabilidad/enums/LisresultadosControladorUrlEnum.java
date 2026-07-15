/*
 * LisresultadosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LisresultadosControladorUrlEnum {

    URL4199("LISRESULTADOSCONTROLADORURL4199", "118002"),

    URL6648("LISRESULTADOSCONTROLADORURL6648", "16067"),

    URL7929("LISRESULTADOSCONTROLADORURL7929", "16071"),

    URL9573("LISRESULTADOSCONTROLADORURL9573", ""),

    URL5322("LISRESULTADOSCONTROLADORURL5322", "7011"),

    URL4828("LISRESULTADOSCONTROLADORURL4828", "4007");

    private final String key;
    private final String value;

    private LisresultadosControladorUrlEnum(String key, String value) {
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
