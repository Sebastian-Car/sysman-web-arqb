/*
* ResumeningresosControladorUrlEnum
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
public enum ResumeningresosControladorUrlEnum {
   
    URL2797("RESUMENINGRESOSCONTROLADORURL2797","16005"),  
    URL3555("RESUMENINGRESOSCONTROLADORURL3555","16021");
        	
    private final String key;
    private final String value;
	
    private  ResumeningresosControladorUrlEnum(String key, String value) {
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
