/*
* FrinformeretencionespracticadasUrlEnum
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
public enum FrinformeretencionespracticadasUrlEnum {
   
	URL0001("FRINFORMERETENCIONESPRACTICADASURLENUM0001","4001"),
	
	URL0002("FRINFORMERETENCIONESPRACTICADASURLENUM0002","16215"),
	
	URL0003("FRINFORMERETENCIONESPRACTICADASURLENUM0003","16217");
        	
        	
	private final String key;
	private final String value;
	
	private  FrinformeretencionespracticadasUrlEnum(String key, String value) {
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
