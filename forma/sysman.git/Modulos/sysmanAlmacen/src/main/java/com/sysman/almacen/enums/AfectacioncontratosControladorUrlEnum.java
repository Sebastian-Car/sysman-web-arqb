/*
* AfectacioncontratosControladorUrlEnum
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
public enum AfectacioncontratosControladorUrlEnum {
   
	URL11959("AFECTACIONCONTRATOSCONTROLADORURL11959","82021"),
	
	URL12000("AFECTACIONCONTRATOSCONTROLADORURL12000","82023"),
	
	URL12012("AFECTACIONCONTRATOSCONTROLADORURL12012","82024"),
	
	URL1812("AFECTACIONCONTRATOSCONTROLADORURL1812","73010"),
	
	URL2143("AFECTACIONCONTRATOSCONTROLADORURL2143","73010");
        	
	private final String key;
	private final String value;
	
	private  AfectacioncontratosControladorUrlEnum(String key, String value) {
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
