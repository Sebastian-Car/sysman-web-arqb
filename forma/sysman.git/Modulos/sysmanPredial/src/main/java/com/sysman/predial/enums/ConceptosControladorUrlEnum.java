/*
 * ConceptosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author JGuerrero
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ConceptosControladorUrlEnum {

    URL0001("CONCEPTOSCONTROLADORURL0001", "38100U"),

    URL0002("CONCEPTOSCONTROLADORURL0002", "38100D"),

    URL0003("CONCEPTOSCONTROLADORURL0003", "38100C"),

    URL7445("CONCEPTOSCONTROLADORURL7445", "38100G"),

    URL6565("CONCEPTOSCONTROLADORURL6565", "4026");

    private final String key;
    private final String value;

    private ConceptosControladorUrlEnum(String key, String value)
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
