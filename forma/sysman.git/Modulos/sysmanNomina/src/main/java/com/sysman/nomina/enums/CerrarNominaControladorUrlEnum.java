/*
 * CerrarNominaControladorUrlEnum
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
public enum CerrarNominaControladorUrlEnum {

    URL5876("CERRARNOMINACONTROLADORURL5876", "7027"),

    URL5219("CERRARNOMINACONTROLADORURL5219", "471008"),

    URL7612("CERRARNOMINACONTROLADORURL7612", "537004"),

    URL6834("CERRARNOMINACONTROLADORURL6834", "471010");

    private final String key;
    private final String value;

    private CerrarNominaControladorUrlEnum(String key, String value)
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
