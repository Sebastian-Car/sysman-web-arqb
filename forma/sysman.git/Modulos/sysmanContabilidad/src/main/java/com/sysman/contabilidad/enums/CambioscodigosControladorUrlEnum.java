/*
 * CambioscodigosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum CambioscodigosControladorUrlEnum {

    URL12212("CAMBIOSCODIGOSCONTROLADORURL12212","16238"),  
    URL10958("CAMBIOSCODIGOSCONTROLADORURL10958","4001"),  
    URL11528("CAMBIOSCODIGOSCONTROLADORURL11528","15005"); 

    private final String key;
    private final String value;

    private  CambioscodigosControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
