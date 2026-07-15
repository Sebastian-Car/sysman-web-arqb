/*
 * ListadoPlanPptalControladorUrlEnum
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
public enum ListadoPlanPptalControladorUrlEnum {

    URL4521("LISTADOPLANPPTALCONTROLADORURL4521", "45016"),

    URL3362("LISTADOPLANPPTALCONTROLADORURL3362", "4001"),

    URL3754("LISTADOPLANPPTALCONTROLADORURL3754", "45014");

    private final String key;
    private final String value;

    private ListadoPlanPptalControladorUrlEnum(String key, String value) {
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
