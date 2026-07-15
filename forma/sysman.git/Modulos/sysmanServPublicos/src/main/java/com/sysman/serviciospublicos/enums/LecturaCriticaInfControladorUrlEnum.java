/*
 * LecturaCriticaInfControladorUrlEnum
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
public enum LecturaCriticaInfControladorUrlEnum {

    URL7150("LECTURACRITICAINFCONTROLADORURL7150", "214020"),

    URL7690("LECTURACRITICAINFCONTROLADORURL7690", "213018"),

    URL6694("LECTURACRITICAINFCONTROLADORURL6694", "118004"),

    URL214030("LECTURACRITICAINFCONTROLADORURL214030", "214030"),

    URL8319("LECTURACRITICAINFCONTROLADORURL8319", "213015");

    private final String key;
    private final String value;

    private LecturaCriticaInfControladorUrlEnum(String key, String value) {
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
