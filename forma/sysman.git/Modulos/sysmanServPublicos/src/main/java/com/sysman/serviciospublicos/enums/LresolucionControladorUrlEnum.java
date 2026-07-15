/*
* LresolucionControladorUrlEnum
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
public enum LresolucionControladorUrlEnum {
   
           	URL7588("LRESOLUCIONCONTROLADORURL7588","227057"),  
             	URL6705("LRESOLUCIONCONTROLADORURL6705","227055"),  
             	URL6065("LRESOLUCIONCONTROLADORURL6065","214029");
        	
	private final String key;
	private final String value;
	
	private  LresolucionControladorUrlEnum(String key, String value) {
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
