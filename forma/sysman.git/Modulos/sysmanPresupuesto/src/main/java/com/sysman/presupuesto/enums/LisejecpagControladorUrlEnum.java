/*
* LisejecpagControladorUrlEnum
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
public enum LisejecpagControladorUrlEnum {
   
           	URL4170("LISEJECPAGCONTROLADORURL4170","94008"),  
           	URL4800("LISEJECPAGCONTROLADORURL4800","94010"),  
           	URL3773("LISEJECPAGCONTROLADORURL3773","4013");
        	
	private final String key;
	private final String value;
	
	private  LisejecpagControladorUrlEnum(String key, String value) {
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
