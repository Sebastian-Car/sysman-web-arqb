/*
* CambiarnombrepredioControladorUrlEnum
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
public enum CambiarnombrepredioControladorUrlEnum {
   
	URL2066("CAMBIARNOMBREPREDIOCONTROLADORURL2066","137001"),
           	
	URL2857("CAMBIARNOMBREPREDIOCONTROLADORURL2857","137003"); 
        	
	private final String key;
	private final String value;
	
	private  CambiarnombrepredioControladorUrlEnum(String key, String value) {
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
