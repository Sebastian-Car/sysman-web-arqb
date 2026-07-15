/*
 * ListaplancomprasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ListaplancomprasControladorUrlEnum {

    URL3015("LISTAPLANCOMPRASCONTROLADORURL3015",
                    "4001"),

    URL3564("LISTAPLANCOMPRASCONTROLADORURL3564",
                    "542001"),

    URL4447("LISTAPLANCOMPRASCONTROLADORURL4447",
                    "542003");

    private final String key;
    private final String value;

    private ListaplancomprasControladorUrlEnum(String key, String value) {
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
