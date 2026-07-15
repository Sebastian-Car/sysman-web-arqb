/*
* RDepreciacionMesPeriodoUrlEnum
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
public enum RDepreciacionMesPeriodoUrlEnum {
   
           	URL3523("RDEPRECIACIONMESPERIODOURL3523","4007"),
           	URL3953("RDEPRECIACIONMESPERIODOURL3953","112002"),
           	URL4525("RDEPRECIACIONMESPERIODOURL4525","112004");
	private final String key;
	private final String value;
	
	private  RDepreciacionMesPeriodoUrlEnum(String key, String value) {
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
