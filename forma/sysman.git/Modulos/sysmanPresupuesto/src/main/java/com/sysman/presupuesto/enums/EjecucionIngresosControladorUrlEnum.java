/*
 * EjecucionIngresosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EjecucionIngresosControladorUrlEnum {

    URL5292("EJECUCIONINGRESOSCONTROLADORURL5292", "4013"),

    URL4804("EJECUCIONINGRESOSCONTROLADORURL4804", "7004"),

    URL4379("EJECUCIONINGRESOSCONTROLADORURL4379", "7013"),

    URL7713("EJECUCIONINGRESOSCONTROLADORURL7713", "20013"),

    URL8426("EJECUCIONINGRESOSCONTROLADORURL8426", "20015"),

    URL9145("EJECUCIONINGRESOSCONTROLADORURL9145", "23010"),

    URL9774("EJECUCIONINGRESOSCONTROLADORURL9774", "23019"),

    URL5631("EJECUCIONINGRESOSCONTROLADORURL5631", "45002"),

    URL6605("EJECUCIONINGRESOSCONTROLADORURL6605", "45004");

    private final String key;
    private final String value;

    private EjecucionIngresosControladorUrlEnum(String key, String value) {
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
