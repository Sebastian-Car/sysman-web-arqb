/*
* FormadepagosControladorUrlEnum
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
public enum TiempoExpiracionControladorUrlEnum {
   
           	URL7918("TIEMPOEXPIRACIONCONTROLADORURL7918","47023"),  
           	
           	URL6228("TIEMPOEXPIRACIONCONTROLADORURL6228","47024"),  
           	
           	URL18625("TIEMPOEXPIRACIONCONTROLADORURL18625",""),
           	
           	URL12241("TIEMPOEXPIRACIONCONTROLADORURL12241","");
        	
	private final String key;
	private final String value;
	
	private  TiempoExpiracionControladorUrlEnum(String key, String value) {
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
