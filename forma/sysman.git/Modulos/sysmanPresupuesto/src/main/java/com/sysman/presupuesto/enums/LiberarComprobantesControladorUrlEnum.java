/*
* LiberarComprobantesControladorUrlEnum
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
public enum LiberarComprobantesControladorUrlEnum {
   
           	URL3039("LIBERARCOMPROBANTESCONTROLADORURL3039","25018"),  
             	URL4206("LIBERARCOMPROBANTESCONTROLADORURL4206","38015");
        	
	private final String key;
	private final String value;
	
	private  LiberarComprobantesControladorUrlEnum(String key, String value) {
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
