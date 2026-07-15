/*
 * NovedadcontratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NovedadcontratosControladorUrlEnum {

    URL9249("NOVEDADCONTRATOSCONTROLADORURL9249",
                    "82077"),

    URL8418("NOVEDADCONTRATOSCONTROLADORURL8418",
                    "82074"),
    
    URL001("NOVEDADCONTRATOSCONTROLADORURL001",
                    "82080"),
    
    URL002("NOVEDADCONTRATOSCONTROLADORURL002",
                    "8200D"),
    
    URL7878("NOVEDADCONTRATOSCONTROLADORURL002",
                    "82082");

    private final String key;
    private final String value;

    private NovedadcontratosControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
