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
public enum CertificadosDianControladorUrlEnum {

    URL2288("CERTIFICADOSDIANCONTROLADOR2288", "471021"),

    URL2289("CERTIFICADOSDIANCONTROLADOR2289", "471022"),

    URL2290("CERTIFICADOSDIANCONTROLADOR2290", "471015"),

    URL2291("CERTIFICADOSDIANCONTROLADOR2291", "210052"),

    URL2292("CERTIFICADOSDIANCONTROLADOR2292", "59020"),
    
    URL0007("CERTIFICADOSDIANCONTROLADOR0007", "210140"),
	
	URL0008("CERTIFICADOSDIANCONTROLADOR0008", "210165");

    private final String key;
    private final String value;

    private CertificadosDianControladorUrlEnum(String key, String value)
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
