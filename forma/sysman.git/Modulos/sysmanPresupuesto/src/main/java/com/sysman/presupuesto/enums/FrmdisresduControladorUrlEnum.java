/*
* FrmdisresduControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmdisresduControladorUrlEnum {
   
           	URL5293("FRMDISRESDUCONTROLADORURL5293","45020"),  
             	URL3940("FRMDISRESDUCONTROLADORURL3940","4002"),  
             	URL4371("FRMDISRESDUCONTROLADORURL4371","45018");
        	
	private final String key;
	private final String value;
	
	private  FrmdisresduControladorUrlEnum(String key, String value) {
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
