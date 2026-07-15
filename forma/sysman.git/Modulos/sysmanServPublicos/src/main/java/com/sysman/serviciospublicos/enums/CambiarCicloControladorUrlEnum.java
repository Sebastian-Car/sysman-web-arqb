/*
* CambiarCicloControladorUrlEnum
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
public enum CambiarCicloControladorUrlEnum {
   
             	URL3143("CAMBIARCICLOCONTROLADORURL3143","214034"),  
             	URL6368("CAMBIARCICLOCONTROLADORURL6368","213034"),
             	URL5380("CAMBIARCICLOCONTROLADORURL5380","213036"), 
             	URL5649("CAMBIARCICLOCONTROLADORURL5649","213037"), 
             	URL2349("CAMBIARCICLOCONTROLADORURL2349","227006"); 
        	
	private final String key;
	private final String value;
	
	private  CambiarCicloControladorUrlEnum(String key, String value) {
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
