/*
 * ListadoFimmRecepcionControladorUrlEnum
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
public enum ListadoFimmRecepcionControladorUrlEnum {

    URL6405("LISTADOFIMMRECEPCIONCONTROLADORURL6405",
                    "227035"),

    URL8036("LISTADOFIMMRECEPCIONCONTROLADORURL8036",
                    "227001"),

    URL7225("LISTADOFIMMRECEPCIONCONTROLADORURL7225",
                    "227035"),

    URL8592("LISTADOFIMMRECEPCIONCONTROLADORURL8592",
                    "227003");

    private final String key;
    private final String value;

    private ListadoFimmRecepcionControladorUrlEnum(String key, String value) {
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
