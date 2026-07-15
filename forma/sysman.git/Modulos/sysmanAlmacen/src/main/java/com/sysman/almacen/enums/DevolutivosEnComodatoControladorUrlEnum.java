/*
* DevolutivosEnComodatoControladorUrlEnum
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
public enum DevolutivosEnComodatoControladorUrlEnum {
   
           	URL2539("DEVOLUTIVOSENCOMODATOCONTROLADORURL2539","112011"),  
             	URL3256("DEVOLUTIVOSENCOMODATOCONTROLADORURL3256","112013");
        	
	private final String key;
	private final String value;
	
	private  DevolutivosEnComodatoControladorUrlEnum(String key, String value) {
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
