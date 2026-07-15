/*
 * LsinmedicionControladorUrlEnum
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
public enum LsinmedicionControladorUrlEnum {

    URL4803("LSINMEDICIONCONTROLADORURL4803",
                    "214076"),

    URL11539("LSINMEDICIONCONTROLADORURL11539",
                    " String nombrePeriodo = service.buscarEnLista( ciclo, \"NUMERO\", \"NOMPERIODO\",");

    private final String key;
    private final String value;

    private LsinmedicionControladorUrlEnum(String key, String value) {
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
