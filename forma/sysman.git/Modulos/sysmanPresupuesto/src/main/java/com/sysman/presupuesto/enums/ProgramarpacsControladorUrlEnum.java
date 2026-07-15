/*
 * ProgramarpacsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ProgramarpacsControladorUrlEnum {
    URL27174("PROGRAMARPACSCONTROLADORURL27601", "128001"),

    URL27175("PROGRAMARPACSCONTROLADORURL27601", "128003"),

    URL27601("PROGRAMARPACSCONTROLADORURL27601", "128004"),

    URL23607("PROGRAMARPACSCONTROLADORURL23607", "128005"), 
    
    URL129005("PROGRAMARPACSCONTROLADORURL129005","129005"),
    
    URL23487("PROGRAMARPACSCONTROLADORURL23607", "29106");

    private final String key;
    private final String value;

    private ProgramarpacsControladorUrlEnum(String key, String value) {
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
