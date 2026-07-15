/*
 * InformeembargoControladorUrlEnum
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
public enum InformeembargoControladorUrlEnum {

    URL3426("INFORMEEMBARGOCONTROLADORURL3426", "471008"),

    URL4243("INFORMEEMBARGOCONTROLADORURL4243", "471028"),

    URL5658("INFORMEEMBARGOCONTROLADORURL5658", "471042");

    private final String key;
    private final String value;

    private InformeembargoControladorUrlEnum(String key, String value)
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
