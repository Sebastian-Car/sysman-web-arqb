/*
 * IncapacidadesControladorUrlEnum
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
public enum IncapacidadesControladorUrlEnum {

    URL8472("INCAPACIDADESCONTROLADORURL8472","622002"),
    URL6321("INCAPACIDADESCONTROLADORURL6321","621001"),
    URL6471("INCAPACIDADESCONTROLADORURL6471","471002"),  
    URL4658("INCAPACIDADESCONTROLADORURL4658","471031"),  
    URL6096("INCAPACIDADESCONTROLADORURL6096","622001"),  
    URL8965("INCAPACIDADESCONTROLADORURL8965","623001"),  
    URL6830("INCAPACIDADESCONTROLADORURL6830","7029");

    private final String key;
    private final String value;

    private  IncapacidadesControladorUrlEnum(String key, String value) {
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
