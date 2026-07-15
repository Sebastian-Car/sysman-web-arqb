/*
 * FrmindicadoresbpsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmindicadoresbpsControladorUrlEnum {

    URL4576("FRMINDICADORESBPSCONTROLADORURL4576","554007"),
    URL6271("FRMINDICADORESBPSCONTROLADORURL6271","552004"),
    URL4786("FRMINDICADORESBPSCONTROLADORURL4786","552003"),
    URL6560("FRMINDICADORESBPSCONTROLADORURL6560","566001"),  
    URL7265("FRMINDICADORESBPSCONTROLADORURL7265","552002");

    private final String key;
    private final String value;

    private  FrmindicadoresbpsControladorUrlEnum(String key, String value) {
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
