/*
* ImpresionPorLotesAlmacenControladorUrlEnum
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
public enum ImpresionPorLotesAlmacenControladorUrlEnum {
   
	URL1717("IMPRESIONPORLOTESALMACENCONTROLADORURL1717","139023"),  
             	
	URL1718("IMPRESIONPORLOTESALMACENCONTROLADORURL1718","139025"),  
             	
	URL1719("IMPRESIONPORLOTESALMACENCONTROLADORURL1719","139027");  
             	

        	
	private final String key;
	private final String value;
	
	private  ImpresionPorLotesAlmacenControladorUrlEnum(String key, String value) {
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
