/*
* UresolucionesControladorUrlEnum
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
public enum UresolucionesControladorUrlEnum {
   
           	URL13925("URESOLUCIONESCONTROLADORURL13925","406005"),  
             	URL12187("URESOLUCIONESCONTROLADORURL12187","385032"),  
             	URL13585("URESOLUCIONESCONTROLADORURL13585","5005"),  
             	URL12977("URESOLUCIONESCONTROLADORURL12977","2004"),  
             	URL14299("URESOLUCIONESCONTROLADORURL14299","406006"),  
             	URL7967("URESOLUCIONESCONTROLADORURL7967","422001");  
        	
	private final String key;
	private final String value;
	
	private  UresolucionesControladorUrlEnum(String key, String value) {
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
