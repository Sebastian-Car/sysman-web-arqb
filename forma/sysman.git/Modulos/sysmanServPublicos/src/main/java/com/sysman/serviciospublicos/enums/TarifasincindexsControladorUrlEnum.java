/*
* TarifasincindexsControladorUrlEnum
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
public enum TarifasincindexsControladorUrlEnum {
   
           	URL3177("TARIFASINCINDEXSCONTROLADORURL3177","229001"),  
             	URL4657("TARIFASINCINDEXSCONTROLADORURL4657","229003"),  
             	URL3482("TARIFASINCINDEXSCONTROLADORURL3482","229002");
        	
	private final String key;
	private final String value;
	
	private  TarifasincindexsControladorUrlEnum(String key, String value) {
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
