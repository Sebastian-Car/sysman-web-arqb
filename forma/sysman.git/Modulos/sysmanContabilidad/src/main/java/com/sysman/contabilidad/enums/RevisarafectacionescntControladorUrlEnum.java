/*
* RevisarafectacionescntControladorUrlEnum
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
public enum RevisarafectacionescntControladorUrlEnum {
   
           	URL5836("REVISARAFECTACIONESCNTCONTROLADORURL5836","16025"),  
             	URL6111("REVISARAFECTACIONESCNTCONTROLADORURL6111","7002"), 
             	URL425("REVISARAFECTACIONESCNTCONTROLADORURL425","4011"),  
             	URL6449("REVISARAFECTACIONESCNTCONTROLADORURL6449","7015");
        	
	private final String key;
	private final String value;
	
	private  RevisarafectacionescntControladorUrlEnum(String key, String value) {
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
