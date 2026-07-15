/*
* InformeconsumomixtoControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum InformeconsumomixtoControladorUrlEnum {
   
           	URL5956("INFORMECONSUMOMIXTOCONTROLADORURL5956","213085"),  
             	URL5343("INFORMECONSUMOMIXTOCONTROLADORURL5343","213083"),  
             	URL4935("INFORMECONSUMOMIXTOCONTROLADORURL4935","214053");
        	
	private final String key;
	private final String value;
	
	private  InformeconsumomixtoControladorUrlEnum(String key, String value) {
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
