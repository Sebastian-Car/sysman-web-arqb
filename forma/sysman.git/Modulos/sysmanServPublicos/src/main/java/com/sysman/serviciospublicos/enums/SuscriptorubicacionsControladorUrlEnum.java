/*
* SuscriptorubicacionsControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum SuscriptorubicacionsControladorUrlEnum {
   
           	URL6037("SUSCRIPTORUBICACIONSCONTROLADORURL6037","214005"),
             	URL6416("SUSCRIPTORUBICACIONSCONTROLADORURL6416","242001"),  
             	URL7592("SUSCRIPTORUBICACIONSCONTROLADORURL7592","214030"),  
             	URL7086("SUSCRIPTORUBICACIONSCONTROLADORURL7086","242003");
        	
	private final String key;
	private final String value;
	
	private  SuscriptorubicacionsControladorUrlEnum(String key, String value) {
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
