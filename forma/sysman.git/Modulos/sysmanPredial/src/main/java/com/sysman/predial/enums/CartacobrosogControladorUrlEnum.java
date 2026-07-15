/*
 * CartacobrosogControladorUrlEnum
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
public enum CartacobrosogControladorUrlEnum {

    URL8228("CARTACOBROSOGCONTROLADORURL8228", "104028"),

    URL5550("CARTACOBROSOGCONTROLADORURL5550", "367013"),

    URL4517("CARTACOBROSOGCONTROLADORURL4517", "4002"),

    URL5032("CARTACOBROSOGCONTROLADORURL5032", "4024"),

    URL6819("CARTACOBROSOGCONTROLADORURL6819", "367015");

    private final String key;
    private final String value;

    private CartacobrosogControladorUrlEnum(String key, String value) {
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
