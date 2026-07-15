/*
* EstadodetesoreriabControladorUrlEnum
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
public enum EstadodetesoreriabControladorUrlEnum {
   
           	URL4076("ESTADODETESORERIABCONTROLADORURL4076","4002"),  
            URL5508("ESTADODETESORERIABCONTROLADORURL5508","16059"),  
            URL4626("ESTADODETESORERIABCONTROLADORURL4626","16057");
        	
	private final String key;
	private final String value;
	
	private  EstadodetesoreriabControladorUrlEnum(String key, String value) {
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
