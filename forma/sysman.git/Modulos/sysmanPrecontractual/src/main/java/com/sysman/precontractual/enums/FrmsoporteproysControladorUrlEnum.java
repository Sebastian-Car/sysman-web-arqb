/*
 * FrmsoporteproysControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmsoporteproysControladorUrlEnum {

    URL0001("FRMSOPORTEPROYSCONTROLADORURL0001", "14001"),

    URL8992("FRMSOPORTEPROYSCONTROLADORURL8992", "61018"),

    URL7648("FRMSOPORTEPROYSCONTROLADORURL7648", "464001"),

    URL6511("FRMSOPORTEPROYSCONTROLADORURL6511", "467001");

    private final String key;
    private final String value;

    private FrmsoporteproysControladorUrlEnum(String key, String value) {
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
