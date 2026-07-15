/*
 * EjecucionMensualIngresosControladorUrlEnum
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
public enum EjecucionMensualIngresosControladorUrlEnum {

    URL4048("EJECUCIONMENSUALINGRESOSCONTROLADORURL4048", "4001"),

    URL4813("EJECUCIONMENSUALINGRESOSCONTROLADORURL4813", "45002"),

    URL4385("EJECUCIONMENSUALINGRESOSCONTROLADORURL4385", "7007"),

    URL5730("EJECUCIONMENSUALINGRESOSCONTROLADORURL5730", "45004");

    private final String key;
    private final String value;

    private EjecucionMensualIngresosControladorUrlEnum(String key,
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
