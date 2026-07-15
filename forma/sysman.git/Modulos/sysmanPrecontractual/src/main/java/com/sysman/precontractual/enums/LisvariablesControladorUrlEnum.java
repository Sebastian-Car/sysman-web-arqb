/*
 * LisvariablesControladorUrlEnum
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
public enum LisvariablesControladorUrlEnum {

    URL3300("LISVARIABLESCONTROLADORURL3300", "525001"),

    URL2742("LISVARIABLESCONTROLADORURL2742", "184001"),

    URL4216("LISVARIABLESCONTROLADORURL4216", "525003");
    private final String key;
    private final String value;

    private LisvariablesControladorUrlEnum(String key, String value)
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
