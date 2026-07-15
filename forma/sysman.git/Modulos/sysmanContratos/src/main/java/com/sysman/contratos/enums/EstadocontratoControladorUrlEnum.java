/*
 * EstadocontratoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum EstadocontratoControladorUrlEnum {

    URL4741("ESTADOCONTRATOCONTROLADORURL4741","73054"),
    URL4715("ESTADOCONTRATOCONTROLADORURL4715","73053"),
    URL1783("ESTADOCONTRATOCONTROLADORURL1783","14067"),
    URL2578("ESTADOCONTRATOCONTROLADORURL2578","14048");

    private final String key;
    private final String value;

    private  EstadocontratoControladorUrlEnum(String key, String value) {
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
