/*
 * ConsolidandosControladorUrlEnum
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
public enum ConsolidandosControladorUrlEnum {

    URL6891("CONSOLIDANDOSCONTROLADORURL6891", "59004"),

    URL6547("CONSOLIDANDOSCONTROLADORURL6547", "4001"),

    URL7823("CONSOLIDANDOSCONTROLADORURL7823", "59001"),

    URL14023("CONSOLIDANDOSCONTROLADORURL14023", "16020"),

    URL13316("CONSOLIDANDOSCONTROLADORURL13316", "64002");

    private final String key;
    private final String value;

    private ConsolidandosControladorUrlEnum(String key, String value) {
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
