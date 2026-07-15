/*
* DesviacionhistoriasControladorUrlEnum
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
public enum DesviacionhistoriasControladorUrlEnum {
    
             	URL7198("DESVIACIONHISTORIASCONTROLADORURL7198","333003"),  
             	URL8562("DESVIACIONHISTORIASCONTROLADORURL8562","334002"),  
             	URL9681("DESVIACIONHISTORIASCONTROLADORURL9681","104017");
        	
	private final String key;
	private final String value;
	
	private  DesviacionhistoriasControladorUrlEnum(String key, String value) {
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
