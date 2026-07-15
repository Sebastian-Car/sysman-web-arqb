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
public enum EstructuraComponenteControladorUrlEnum {
   
           	URL3238("ESTRUCTURACOMPONENTECONTROLADORURL3238","112140"),  
           	
             	URL4991("ESTRUCTURACOMPONENTECONTROLADORURL4991","62015"),
             	
             	URL4055("ESTRUCTURACOMPONENTECONTROLADORURL4055","112013"),
             	
             	URL5683("ESTRUCTURACOMPONENTECONTROLADORURL5683","62013"),
             	
             	URL5684("ESTRUCTURACOMPONENTECONTROLADORURL5684","171800G"),
             	
             	URL5685("ESTRUCTURACOMPONENTECONTROLADORURL5685","1005"),
             	
             	URL5686("ESTRUCTURACOMPONENTECONTROLADORURL5685","2009"),
             	
             	URL5687("ESTRUCTURACOMPONENTECONTROLADORURL5687","5001")
             	
             	;
        	
	private final String key;
	private final String value;
	
	private  EstructuraComponenteControladorUrlEnum(String key, String value) {
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
