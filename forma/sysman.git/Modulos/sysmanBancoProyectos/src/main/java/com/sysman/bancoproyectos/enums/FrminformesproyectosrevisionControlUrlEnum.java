/*
 * FrminformesproyectosrevisionControlUrlEnum
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
public enum FrminformesproyectosrevisionControlUrlEnum {

    URL4310("FRMINFORMESPROYECTOSREVISIONCONTROLURL4310", "4001"),

    URL4737("FRMINFORMESPROYECTOSREVISIONCONTROLURL4737", "4027"),

    URL5347("FRMINFORMESPROYECTOSREVISIONCONTROLURL5347", "32020"),

    URL7620("FRMINFORMESPROYECTOSREVISIONCONTROLURL7620", "32022"),

    URL0001("FRMINFORMESPROYECTOSREVISIONCONTROLURL0001", "118016");

    private final String key;
    private final String value;

    private FrminformesproyectosrevisionControlUrlEnum(String key,
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
