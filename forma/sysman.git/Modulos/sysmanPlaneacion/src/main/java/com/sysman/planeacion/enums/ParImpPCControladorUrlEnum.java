/*
 * ParImpPCControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ParImpPCControladorUrlEnum {

    URL8024("PARIMPPCCONTROLADORURL8024", "118022"),

    URL5956("PARIMPPCCONTROLADORURL5956", "62002"),

    URL8694("PARIMPPCCONTROLADORURL8694", "62001"),

    URL6653("PARIMPPCCONTROLADORURL6653", "62011"),

    URL7321("PARIMPPCCONTROLADORURL7321", "545002");

    private final String key;
    private final String value;

    private ParImpPCControladorUrlEnum(String key, String value) {
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
