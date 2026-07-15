/*
 * ImprimeContratosControladorUrlEnum
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
public enum ImprimeContratosControladorUrlEnum {

    URL6762("IMPRIMECONTRATOSCONTROLADORURL6762", "82052"),

    URL6763("IMPRIMECONTRATOSCONTROLADORURL6763", "82054"),

    URL8554("IMPRIMECONTRATOSCONTROLADORURL8554", "104038"),

    URL8061("IMPRIMECONTRATOSCONTROLADORURL8061", "62030"),

    URL7394("IMPRIMECONTRATOSCONTROLADORURL7394", "82056"),

    URL6044("IMPRIMECONTRATOSCONTROLADORURL6044", "73046");

    private final String key;
    private final String value;

    private ImprimeContratosControladorUrlEnum(String key, String value) {
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
