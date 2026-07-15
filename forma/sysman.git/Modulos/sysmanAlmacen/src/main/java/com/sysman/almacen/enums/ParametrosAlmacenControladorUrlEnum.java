/*
 * ParametrosAlmacenControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ParametrosAlmacenControladorUrlEnum {

    URL7666("PARAMETROSALMACENCONTROLADORURL7666",
                    "7021"),
    
    URL7476("PARAMETROSALMACENCONTROLADORURL7476",
                    "59003"),

    URL8736("PARAMETROSALMACENCONTROLADORURL8736",
                    "112207"),

    URL7979("PARAMETROSALMACENCONTROLADORURL7979",
                    "112205"),
    
    URL1984001("PARAMETROSALMACENCONTROLADORURL", "1984001"),
    
    URL1984002("PARAMETROSALMACENCONTROLADORURL", "1984002"),
    
    URL59030("PARAMETROSALMACENCONTROLADORURL59030", "59030");
    
    

    private final String key;
    private final String value;

    private ParametrosAlmacenControladorUrlEnum(String key, String value) {
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
