/*
 * CargosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CargosControladorUrlEnum {
    URL002("CARGOSCONTROLADORURL8796", "732001"),

    URL2288("CARGOSCONTROLADORURL2288", "462001"),

    URL143("CARGOSCONTROLADORURL143", "463023"),
    
    URL185("CARGOSCONTROLADORURL185", "1778001");

    private final String key;
    private final String value;

    private CargosControladorUrlEnum(String key, String value) {
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
