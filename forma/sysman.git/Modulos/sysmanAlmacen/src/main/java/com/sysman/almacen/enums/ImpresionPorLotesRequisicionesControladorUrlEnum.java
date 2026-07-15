/*
* ImpresionPorLotesRequisicionesControladorUrlEnum
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
public enum ImpresionPorLotesRequisicionesControladorUrlEnum {
   
           	URL2992("IMPRESIONPORLOTESREQUISICIONESCONTROLADORURL2992","109014"),  
             	URL2458("IMPRESIONPORLOTESREQUISICIONESCONTROLADORURL2458","109013");
        	
	private final String key;
	private final String value;
	
	private  ImpresionPorLotesRequisicionesControladorUrlEnum(String key, String value) {
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
