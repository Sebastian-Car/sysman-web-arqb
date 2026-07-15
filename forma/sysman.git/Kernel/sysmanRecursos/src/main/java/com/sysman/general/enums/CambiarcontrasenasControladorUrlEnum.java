/*
* CambiarcontrasenasControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum CambiarcontrasenasControladorUrlEnum {
	URL4379("CAMBIARCONTRASENASCONTROLADORURL4379","47003"),
           	URL2395("CAMBIARCONTRASENASCONTROLADORURL2395","47001");
        	
	private final String key;
	private final String value;
	
	private  CambiarcontrasenasControladorUrlEnum(String key, String value) {
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
