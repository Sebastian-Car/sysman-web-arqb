/*
 * BalanceAnualControladorUrlEnum
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
public enum BalanceAnualControladorUrlEnum {

    URL3335("BALANCEANUALCONTROLADORURL3335", "16007"),

    URL4635("BALANCEANUALCONTROLADORURL4635", "16010"),

    URL3620("BALANCEANUALCONTROLADORURL3620", "16008");

    private final String key;
    private final String value;

    private BalanceAnualControladorUrlEnum(String key, String value) {
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
