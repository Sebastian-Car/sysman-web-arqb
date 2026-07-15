/*
* ResponsableControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ResponsableControladorUrlEnum {
   
           	URL6809("RESPONSABLECONTROLADORURL6809","62002"),  
             	URL5035("RESPONSABLECONTROLADORURL5035","61005"),  
             	URL6115("RESPONSABLECONTROLADORURL6115","62002");
        	
	private final String key;
	private final String value;
	
	private  ResponsableControladorUrlEnum(String key, String value) {
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
