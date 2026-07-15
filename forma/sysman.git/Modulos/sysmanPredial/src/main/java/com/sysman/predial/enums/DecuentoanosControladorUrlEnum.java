/*
 * DecuentoanosControladorUrlEnum
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
public enum DecuentoanosControladorUrlEnum {

    URL001("DECUENTOANOSCONTROLADORURL001", "4025"),

    URL3330("DECUENTOANOSCONTROLADORURL3330", "7016"),

    URL2945("DECUENTOANOSCONTROLADORURL2945", "4001");

    private final String key;
    private final String value;

    private DecuentoanosControladorUrlEnum(String key, String value) {
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
