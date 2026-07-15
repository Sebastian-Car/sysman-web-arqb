/*
* SubpredioavaluosControladorUrlEnum
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
public enum SubpredioavaluosControladorUrlEnum {
   
           	URL29539("SUBPREDIOAVALUOSCONTROLADORURL29539","137020"),  
             	URL7017("SUBPREDIOAVALUOSCONTROLADORURL7017","141081"),  
             	URL21791("SUBPREDIOAVALUOSCONTROLADORURL21791","152003"),  
             	URL10260("SUBPREDIOAVALUOSCONTROLADORURL10260","152004"),  
             	URL6623("SUBPREDIOAVALUOSCONTROLADORURL6623","4013"),  
             	URL1072("SUBPREDIOAVALUOSCONTROLADORURL1072","152002");
        	
	private final String key;
	private final String value;
	
	private  SubpredioavaluosControladorUrlEnum(String key, String value) {
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
