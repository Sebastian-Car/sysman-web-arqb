/*
* ListadoActaSuspencionUrlEnum
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
public enum ListadoActaSuspencionUrlEnum {
   
           	URL9995("LISTADOACTASUSPENCIONURL9995","214092"),  
             	URL6363("LISTADOACTASUSPENCIONURL6363","213126"),  
             	URL8368("LISTADOACTASUSPENCIONURL8368","364002"),  
             	URL9222("LISTADOACTASUSPENCIONURL9222","364003"),  
             	URL7450("LISTADOACTASUSPENCIONURL7450","213128");
        	
	private final String key;
	private final String value;
	
	private  ListadoActaSuspencionUrlEnum(String key, String value) {
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
