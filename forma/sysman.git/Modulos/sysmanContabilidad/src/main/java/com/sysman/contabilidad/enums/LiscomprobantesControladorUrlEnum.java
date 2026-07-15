/*
* LiscomprobantesControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum LiscomprobantesControladorUrlEnum {
   
           	URL4731("LISCOMPROBANTESCONTROLADORURL4731","14036"),  
            URL3394("LISCOMPROBANTESCONTROLADORURL3394","15005"),  
            URL3975("LISCOMPROBANTESCONTROLADORURL3975","15003"),  
            URL5321("LISCOMPROBANTESCONTROLADORURL5321","14048");
        	
	private final String key;
	private final String value;
	
	private  LiscomprobantesControladorUrlEnum(String key, String value) {
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
