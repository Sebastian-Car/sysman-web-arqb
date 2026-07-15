/*
 * ReportenotificacionescobroControladorUrlEnum
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
public enum ReportenotificacionescobroControladorUrlEnum {

    URL6580("REPORTENOTIFICACIONESCOBROCONTROLADORURL6580", "416005"),

    URL7511("REPORTENOTIFICACIONESCOBROCONTROLADORURL7511", "416007"),

    URL5062("REPORTENOTIFICACIONESCOBROCONTROLADORURL5062", "416001"),

    URL5819("REPORTENOTIFICACIONESCOBROCONTROLADORURL5819", "416003");

    private final String key;
    private final String value;

    private ReportenotificacionescobroControladorUrlEnum(String key,
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
