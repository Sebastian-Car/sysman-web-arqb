/*
 * FinanciablesControladorUrlEnum
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
public enum FinanciablesControladorUrlEnum {

    URL8301("FINANCIABLESCONTROLADORURL8301", "151019"),

    URL7525("FINANCIABLESCONTROLADORURL7525", "210137");

    private final String key;
    private final String value;

    private FinanciablesControladorUrlEnum(String key, String value)
    {
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
