/*
 * ResumenRecaudosConceptoDControladorUrlEnum
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
public enum ResumenRecaudosConceptoDControladorUrlEnum {

    URL5292("RESUMENRECAUDOSCONCEPTODCONTROLADORURL5292",
                    "227051"),

    URL6363("RESUMENRECAUDOSCONCEPTODCONTROLADORURL6363",
                    "227052");

    private final String key;
    private final String value;

    private ResumenRecaudosConceptoDControladorUrlEnum(String key,
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
