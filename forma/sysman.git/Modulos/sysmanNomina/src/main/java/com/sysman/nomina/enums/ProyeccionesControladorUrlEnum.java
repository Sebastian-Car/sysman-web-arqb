/*
 * ProyeccionesControladorUrlEnum
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
public enum ProyeccionesControladorUrlEnum {
    
    URL7541("PROYECCIONESCONTROLADORURL7541","210040"),
    URL6145("PROYECCIONESCONTROLADORURL6145","210042"),
    URL5889("PROYECCIONESCONTROLADORURL5889","653002"),  
    URL13110("PROYECCIONESCONTROLADORURL13110","653001"),  
    URL14482("PROYECCIONESCONTROLADORURL14482","151021");

    private final String key;
    private final String value;

    private  ProyeccionesControladorUrlEnum(String key, String value) {
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
