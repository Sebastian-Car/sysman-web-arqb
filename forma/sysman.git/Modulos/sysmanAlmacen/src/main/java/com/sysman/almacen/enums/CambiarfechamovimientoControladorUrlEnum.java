/*
* CambiarfechamovimientoControladorUrlEnum
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
public enum CambiarfechamovimientoControladorUrlEnum {
   
           	URL4276("CAMBIARFECHAMOVIMIENTOCONTROLADORURL4276","41004"),  
             	URL2397("CAMBIARFECHAMOVIMIENTOCONTROLADORURL2397","139001");
        	
	private final String key;
	private final String value;
	
	private  CambiarfechamovimientoControladorUrlEnum(String key, String value) {
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
