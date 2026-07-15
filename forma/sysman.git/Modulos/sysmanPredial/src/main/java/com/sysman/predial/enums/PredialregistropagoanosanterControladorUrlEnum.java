/*
 * PredialregistropagoanosanterControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PredialregistropagoanosanterControladorUrlEnum {

    URL6306("PREDIALREGISTROPAGOANOSANTERCONTROLADORURL6306",
                    "376010"),

    URL4450("PREDIALREGISTROPAGOANOSANTERCONTROLADORURL4450",
                    "4001"),

    URL4832("PREDIALREGISTROPAGOANOSANTERCONTROLADORURL4832",
                    "375004"),

    URL5356("PREDIALREGISTROPAGOANOSANTERCONTROLADORURL5356",
                    "367153");

    private final String key;
    private final String value;

    private PredialregistropagoanosanterControladorUrlEnum(String key,
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
