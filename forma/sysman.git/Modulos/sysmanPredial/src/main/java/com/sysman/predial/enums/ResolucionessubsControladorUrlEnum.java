/*
 * ResolucionessubsControladorUrlEnum
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
public enum ResolucionessubsControladorUrlEnum {
    URL4703("RESOLUCIONESSUBSCONTROLADORURLURL4703", "422003"),

    URL70824("RESOLUCIONESSUBSCONTROLADORURLURL70824", "422004"),

    URL70836("RESOLUCIONESSUBSCONTROLADORURLURL70836", "367200"),

    URL38366("RESOLUCIONESSUBSCONTROLADORURLURL38366", "422002"),

    URL21646("RESOLUCIONESSUBSCONTROLADORURLURL21646", "367199"),

    URL19743("RESOLUCIONESSUBSCONTROLADORURLURL19743", "367198"),

    URL18296("RESOLUCIONESSUBSCONTROLADORURLURL18296", "385033"),

    URL19822("RESOLUCIONESSUBSCONTROLADORURLURL19822", "367196"),

    URL18375("RESOLUCIONESSUBSCONTROLADORURLURL18375", "367194"),

    URL17577("RESOLUCIONESSUBSCONTROLADORURLURL17577", "376014"),

    URL13490("RESOLUCIONESSUBSCONTROLADORURLURL13490", "4001"),

    URL12897("RESOLUCIONESSUBSCONTROLADORURLURL12897", "118010"),

    URL12369("RESOLUCIONESSUBSCONTROLADORURLURL12369", "378003");

    private final String key;
    private final String value;

    private ResolucionessubsControladorUrlEnum(String key, String value)
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
