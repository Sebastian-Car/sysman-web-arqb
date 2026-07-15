/*
* DepenControladorUrlEnum
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
public enum DepreciarComponenteControladorUrlEnum {
   
           	 
             	URL3270("DEPRECIARCOMPONENTECONTROLADORURL3270","1714001"),
             	
             	URL3271("DEPRECIARCOMPONENTECONTROLADORURL3271","4002"), //AÑO
             	
             	URL3272("DEPRECIARCOMPONENTECONTROLADORURL3272","7001"), //MES
             	
             	URL3273("DEPRECIARCOMPONENTECONTROLADORURL3273","1714003")
             	;
        	
	private final String key;
	private final String value;
	
	private  DepreciarComponenteControladorUrlEnum(String key, String value) {
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
