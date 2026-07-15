/*
 * ListadosRtefteControladorUrlEnum
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
public enum ListadosRtefteControladorUrlEnum {

    URL5407("LISTADOSRTEFTECONTROLADORURL5407",
                    "471002"),

    URL5840("LISTADOSRTEFTECONTROLADORURL5840",
                    "471034"),

    URL6969("LISTADOSRTEFTECONTROLADORURL6969",
                    "471041"),

    URL7449("LISTADOSRTEFTECONTROLADORURL7449",
                    "537001"),

    URL8146("LISTADOSRTEFTECONTROLADORURL8146",
                    "628001");

    private final String key;
    private final String value;

    private ListadosRtefteControladorUrlEnum(String key, String value) {
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
