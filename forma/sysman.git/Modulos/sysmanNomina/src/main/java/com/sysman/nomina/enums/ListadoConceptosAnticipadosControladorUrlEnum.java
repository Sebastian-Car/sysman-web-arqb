/*
 * ListadoConceptosAnticipadosControladorUrlEnum
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
public enum ListadoConceptosAnticipadosControladorUrlEnum {

    URL6444("LISTADOCONCEPTOSANTICIPADOSCONTROLADORURL6444", "471010"),

    URL5768("LISTADOCONCEPTOSANTICIPADOSCONTROLADORURL5768", "7027"),

    URL4910("LISTADOCONCEPTOSANTICIPADOSCONTROLADORURL4910", "471008");

    private final String key;
    private final String value;

    private ListadoConceptosAnticipadosControladorUrlEnum(String key,
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
