/*
 * FrmactdatospersonaldetalladoautsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmactdatospersonaldetalladoautsControladorUrlEnum {

    URL001("LISTADOTIPOSDOCUMENTO", "1001"),

    URL002("LISTADOTIPOSDOCUMENTO", "5001"),

    URL003("LISTADODEPARTAMENTOS", "2001"),

    ;

    private final String key;
    private final String value;

    private FrmactdatospersonaldetalladoautsControladorUrlEnum(String key,
        String value)
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
