/*
* ExistenciadevxdepccControladorUrlEnum
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
public enum ExistenciadevxdepccControladorUrlEnum {
   
           	URL3238("EXISTENCIADEVXDEPCCCONTROLADORURL3238","112011"),  
             	URL4991("EXISTENCIADEVXDEPCCCONTROLADORURL4991","62015"),  
             	URL4055("EXISTENCIADEVXDEPCCCONTROLADORURL4055","112013"),  
             	URL5683("EXISTENCIADEVXDEPCCCONTROLADORURL5683","62013");
        	
	private final String key;
	private final String value;
	
	private  ExistenciadevxdepccControladorUrlEnum(String key, String value) {
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
