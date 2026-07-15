/*
* LdesvioSignificativoUrlEnum
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
public enum LdesvioSignificativoUrlEnum {
   
           	URL5626("LDESVIOSIGNIFICATIVOURL5626","227001"),  
             	URL4447("LDESVIOSIGNIFICATIVOURL4447","214026"),  
             	URL4910("LDESVIOSIGNIFICATIVOURL4910","227002");
        	
	private final String key;
	private final String value;
	
	private  LdesvioSignificativoUrlEnum(String key, String value) {
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
