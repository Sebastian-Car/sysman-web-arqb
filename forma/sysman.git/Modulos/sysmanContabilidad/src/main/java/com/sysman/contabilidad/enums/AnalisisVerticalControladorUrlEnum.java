/*
 * AnalisisVerticalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AnalisisVerticalControladorUrlEnum {

    URL4910("ANALISISVERTICALCONTROLADORURL4910", "16008"),

    URL5995("ANALISISVERTICALCONTROLADORURL5995", "16010"),

    URL4509("ANALISISVERTICALCONTROLADORURL4509", "7002"),

    URL4219("ANALISISVERTICALCONTROLADORURL4219", "16007");

    private final String key;
    private final String value;

    private AnalisisVerticalControladorUrlEnum(String key, String value) {
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
