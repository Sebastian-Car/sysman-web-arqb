/*
 * ListadoParafiscalesControladorUrlEnum
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
public enum ListadoParafiscalesControladorUrlEnum {

    URL4649("LISTADOPARAFISCALESCONTROLADORURL4649",
                    "471040"),

    URL3846("LISTADOPARAFISCALESCONTROLADORURL3846",
                    "471018"),

    URL3196("LISTADOPARAFISCALESCONTROLADORURL3196",
                    "471008"),

    URL5150("LISTADOPARAFISCALESCONTROLADORURL5150",
                    "537003");

    private final String key;
    private final String value;

    private ListadoParafiscalesControladorUrlEnum(String key, String value) {
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
