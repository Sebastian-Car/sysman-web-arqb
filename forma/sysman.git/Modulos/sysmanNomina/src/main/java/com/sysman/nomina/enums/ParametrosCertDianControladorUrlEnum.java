/*
 * CargosControladorUrlEnum
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
public enum ParametrosCertDianControladorUrlEnum {

    URL2288("CERTIFICADOSDIANCONTROLADOR2288", "67009"),

    URL2289("CERTIFICADOSDIANCONTROLADOR2289", "68001"),

    URL2290("CERTIFICADOSDIANCONTROLADOR2290", "6700U"),

    URL2291("CERTIFICADOSDIANCONTROLADOR2291", "");

    private final String key;
    private final String value;

    private ParametrosCertDianControladorUrlEnum(String key, String value)
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
