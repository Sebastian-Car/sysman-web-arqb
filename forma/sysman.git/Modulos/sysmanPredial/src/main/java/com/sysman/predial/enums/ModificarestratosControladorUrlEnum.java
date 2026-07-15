/*
 * ModificarestratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ModificarestratosControladorUrlEnum {

    URL4284("MODIFICARESTRATOSCONTROLADORURL4284",
                    "367120"), URL3849(
                                    "MODIFICARESTRATOSCONTROLADORURL3849",
                                    "377002"), URL3348(
                                                    "MODIFICARESTRATOSCONTROLADORURL3348", "383003");
    private final String key;
    private final String value;

    private ModificarestratosControladorUrlEnum(String key, String value)
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
