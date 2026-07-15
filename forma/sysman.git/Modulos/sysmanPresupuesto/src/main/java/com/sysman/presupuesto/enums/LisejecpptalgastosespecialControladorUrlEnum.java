/*
* LisejecpptalgastosespecialControladorUrlEnum
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
public enum LisejecpptalgastosespecialControladorUrlEnum {
             	
             	URL7977("LISEJECPPTALGASTOSESPECIALCONTROLADORURL7977","29094"),  
             	URL8766("LISEJECPPTALGASTOSESPECIALCONTROLADORURL8766","29096"),  
             	URL7476("LISEJECPPTALGASTOSESPECIALCONTROLADORURL7476","7007"),  
             	URL7142("LISEJECPPTALGASTOSESPECIALCONTROLADORURL7142","4001");
        	
	private final String key;
	private final String value;
	
	private  LisejecpptalgastosespecialControladorUrlEnum(String key, String value) {
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
