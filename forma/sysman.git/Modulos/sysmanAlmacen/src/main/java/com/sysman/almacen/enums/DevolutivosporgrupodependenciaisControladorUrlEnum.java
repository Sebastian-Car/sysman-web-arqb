/*
* DevolutivosporgrupodependenciaisControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum DevolutivosporgrupodependenciaisControladorUrlEnum {
   
	URL6857("DEVOLUTIVOSPORGRUPODEPENDENCIAISCONTROLADORURL6857","62013"),  
             	
	URL4041("DEVOLUTIVOSPORGRUPODEPENDENCIAISCONTROLADORURL4041","112021"),  
             	
	URL6016("DEVOLUTIVOSPORGRUPODEPENDENCIAISCONTROLADORURL6016","62015"),  
             	
	URL4907("DEVOLUTIVOSPORGRUPODEPENDENCIAISCONTROLADORURL4907","112023");
        	
	private final String key;
	private final String value;
	
	private  DevolutivosporgrupodependenciaisControladorUrlEnum(String key, String value) {
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
