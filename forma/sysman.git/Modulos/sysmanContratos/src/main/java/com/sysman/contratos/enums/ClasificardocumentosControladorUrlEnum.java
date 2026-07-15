/*
 * ClasificardocumentosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ClasificardocumentosControladorUrlEnum {

    URL5258("CLASIFICARDOCUMENTOSCONTROLADORURL5258",
                    "73012"),

    URL4671("CLASIFICARDOCUMENTOSCONTROLADORURL4671",
                    "427001"),

    URL4545("CLASIFICARDOCUMENTOSCONTROLADORURL4545",
                    "190004"),

    URL001("CLASIFICARDOCUMENTOSCONTROLADORURL001",
                    "19000C"),

    URL002("CLASIFICARDOCUMENTOSCONTROLADORURL002",
                    "19000U"),

    URL003("CLASIFICARDOCUMENTOSCONTROLADORURL003",
                    "19000D");

    private final String key;
    private final String value;

    private ClasificardocumentosControladorUrlEnum(String key, String value) {
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
