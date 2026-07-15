/*
 * PredialrecibosdepagoControladorUrlEnum
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
public enum PredialrecibosdepagoControladorUrlEnum {

    URL0001("PREDIALRECIBOSDEPAGOCONTROLADORURL0001", "411001"),

    URL7811("PREDIALRECIBOSDEPAGOCONTROLADORURL7811", "374023"),

    URL7372("PREDIALRECIBOSDEPAGOCONTROLADORURL7372", "375001");

    private final String key;
    private final String value;

    private PredialrecibosdepagoControladorUrlEnum(String key, String value) {
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
