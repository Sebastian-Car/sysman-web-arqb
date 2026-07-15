/*
* TercerosAportantesControladorUrlEnum
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
public enum TercerosAportantesControladorUrlEnum {
   
	URL4017("TERCEROSAPORTANTESCONTROLADORURL4017","208001"),  
           	URL4435("TERCEROSAPORTANTESCONTROLADORURL4435","14001");
	private final String key;
	private final String value;
	
	private  TercerosAportantesControladorUrlEnum(String key, String value) {
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
