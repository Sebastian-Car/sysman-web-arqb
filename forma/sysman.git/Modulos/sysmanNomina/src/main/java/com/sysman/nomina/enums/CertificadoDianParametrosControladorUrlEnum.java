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
public enum CertificadoDianParametrosControladorUrlEnum {

    URL2288("CERTIFICADOSDIANPARAMETROSCONTROLADOR2288", "67009"),

    URL2289("CERTIFICADOSDIANPARAMETROSCONTROLADOR2289", "471022"),

    URL2290("CERTIFICADOSDIANPARAMETROSCONTROLADOR2290", "471015"),

    URL2291("CERTIFICADOSDIANPARAMETROSCONTROLADOR2291", "210052");

    private final String key;
    private final String value;

    private CertificadoDianParametrosControladorUrlEnum(String key, String value)
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
