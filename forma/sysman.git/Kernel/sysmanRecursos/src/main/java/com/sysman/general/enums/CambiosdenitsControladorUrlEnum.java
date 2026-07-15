/*
 * CambiosdenitsControladorUrlEnum
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
public enum CambiosdenitsControladorUrlEnum {

    URL3776("CAMBIOSDENITSCONTROLADORURL3776", "9000G"),

    URL5926("CAMBIOSDENITSCONTROLADORURL5926", "14158"),

    URL12329("CAMBIOSDENITSCONTROLADORURL12329", "14005"),

    URL912("CAMBIOSDENITSCONTROLADORURL912", "14172"),

    URL955("CAMBIOSDENITSCONTROLADORURL955", "90002"),
	
	URL903("CAMBIOSDENITSCONTROLADORURL903", "90003");
    ;

    private final String key;
    private final String value;

    private CambiosdenitsControladorUrlEnum(String key, String value)
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
