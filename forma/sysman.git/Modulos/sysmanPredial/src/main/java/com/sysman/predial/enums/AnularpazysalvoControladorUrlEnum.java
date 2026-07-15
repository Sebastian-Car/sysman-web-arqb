/*
* AnularpazysalvoControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum AnularpazysalvoControladorUrlEnum {
   
           	URL3711("ANULARPAZYSALVOCONTROLADORURL3711","373003"),  
             	URL2708("ANULARPAZYSALVOCONTROLADORURL2708","373001");
        	
	private final String key;
	private final String value;
	
	private  AnularpazysalvoControladorUrlEnum(String key, String value) {
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
