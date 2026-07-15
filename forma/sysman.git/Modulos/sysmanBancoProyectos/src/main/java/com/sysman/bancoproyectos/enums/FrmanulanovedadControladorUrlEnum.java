/*
 * FrmanulanovedadControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmanulanovedadControladorUrlEnum {

    URL3116("FRMANULANOVEDADCONTROLADORURL3116", "218005"),

    URL10257("FRMANULANOVEDADCONTROLADORURL10257", "218007"),

    URL3454("FRMANULANOVEDADCONTROLADORURL3454", "130009"),

    URL4532("FRMANULANOVEDADCONTROLADORURL4532", "62055");

    private final String key;
    private final String value;

    private FrmanulanovedadControladorUrlEnum(String key, String value) {
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
