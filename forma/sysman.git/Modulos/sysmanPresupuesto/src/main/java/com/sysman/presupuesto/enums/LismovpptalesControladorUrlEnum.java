/*
* LismovpptalesControladorUrlEnum
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
public enum LismovpptalesControladorUrlEnum {
   
           	URL3492("LISMOVPPTALESCONTROLADORURL3492","25008"),  
           	URL4117("LISMOVPPTALESCONTROLADORURL4117","25012");
        	
	private final String key;
	private final String value;
	
	private  LismovpptalesControladorUrlEnum(String key, String value) {
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
