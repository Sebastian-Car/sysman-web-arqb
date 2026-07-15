/*
 * ResumenConceptosControladorUrlEnum
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
public enum ResumenConceptosControladorUrlEnum {

    URL4753("RESUMENCONCEPTOSCONTROLADORURL4753",
                    "471046"),

    URL2846("RESUMENCONCEPTOSCONTROLADORURL2846",
                    "471002"),

    URL3599("RESUMENCONCEPTOSCONTROLADORURL3599",
                    "471039");

    private final String key;
    private final String value;

    private ResumenConceptosControladorUrlEnum(String key, String value) {
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
