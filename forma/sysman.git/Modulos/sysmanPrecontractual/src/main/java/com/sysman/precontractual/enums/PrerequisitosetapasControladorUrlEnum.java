/*
 * PrerequisitosetapasControladorUrlEnum
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
public enum PrerequisitosetapasControladorUrlEnum {

    URL6244("PREREQUISITOSETAPASCONTROLADORURL6244", "532001"),

    URL4770("PREREQUISITOSETAPASCONTROLADORURL4770", "528001");
    private final String key;
    private final String value;

    private PrerequisitosetapasControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
