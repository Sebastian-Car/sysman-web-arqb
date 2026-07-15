/*
* PedirDocumentoAfectarControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum PedirDocumentoAfectarControladorUrlEnum {
   
           	URL7752("PEDIRDOCUMENTOAFECTARCONTROLADORURL7752","75026"),  
             	URL8770("PEDIRDOCUMENTOAFECTARCONTROLADORURL8770","25024"),
               	URL725("PEDIRDOCUMENTOAFECTARCONTROLADORURL725","75029");
        	
	private final String key;
	private final String value;
	
	private  PedirDocumentoAfectarControladorUrlEnum(String key, String value) {
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
