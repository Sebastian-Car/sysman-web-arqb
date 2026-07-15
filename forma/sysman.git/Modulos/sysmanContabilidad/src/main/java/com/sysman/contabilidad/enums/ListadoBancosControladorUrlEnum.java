/*
* ListadoBancosControladorUrlEnum
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
public enum ListadoBancosControladorUrlEnum {
   
           	URL3500("LISTADOBANCOSCONTROLADORURL3500","15005"),  
             	URL4468("LISTADOBANCOSCONTROLADORURL4468","15003");
        	
	private final String key;
	private final String value;
	
	private  ListadoBancosControladorUrlEnum(String key, String value) {
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
