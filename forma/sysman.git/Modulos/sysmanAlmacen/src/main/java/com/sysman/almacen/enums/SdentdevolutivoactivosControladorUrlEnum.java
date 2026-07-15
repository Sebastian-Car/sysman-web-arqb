/*
* SdentdevolutivoactivosControladorUrlEnum
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
public enum SdentdevolutivoactivosControladorUrlEnum {
   
           	URL7877("SDENTDEVOLUTIVOACTIVOSCONTROLADORURL7877","141077"),
             	URL5842("SDENTDEVOLUTIVOACTIVOSCONTROLADORURL5842","171001"),  
             	URL6418("SDENTDEVOLUTIVOACTIVOSCONTROLADORURL6418","141077"),
             	URL6426("SDENTDEVOLUTIVOACTIVOSCONTROLADORURL6426","161003");
        	
	private final String key;
	private final String value;
	
	private  SdentdevolutivoactivosControladorUrlEnum(String key, String value) {
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
