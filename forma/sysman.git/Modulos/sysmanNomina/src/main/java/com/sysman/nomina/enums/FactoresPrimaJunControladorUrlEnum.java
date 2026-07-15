/*
 * FactoresPrimaJunControladorUrlEnum
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
public enum FactoresPrimaJunControladorUrlEnum {

    URL4850("FACTORESPRIMAJUNCONTROLADORURL4850","471028"),  
    URL4149("FACTORESPRIMAJUNCONTROLADORURL4149","471008"),  
    URL6147("FACTORESPRIMAJUNCONTROLADORURL6147","471010");

    private final String key;
    private final String value;

    private  FactoresPrimaJunControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
