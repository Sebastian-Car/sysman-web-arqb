/*
 * CambiarnombrepredioControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ComponentesElementoControladorUrlEnum {

    URL2066("COMPONENTESELEMENTOCONTROLADORURL2066", "1714001"),

    URL2857("COMPONENTESELEMENTOCONTROLADORURL2857", "141119"),

    URL2858("COMPONENTESELEMENTOCONTROLADORURL2857", "141121"),

    URL2859("COMPONENTESELEMENTOCONTROLADORURL2859", "4070");

    private final String key;
    private final String value;

    private ComponentesElementoControladorUrlEnum(String key, String value)
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
