/*
* LiscomppptalesdControladorUrlEnum
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
public enum LiscomppptalesdControladorUrlEnum {
   
           	URL4694("LISCOMPPPTALESDCONTROLADORURL4694","25012"),  
             	URL3980("LISCOMPPPTALESDCONTROLADORURL3980","25008");
        	
	private final String key;
	private final String value;
	
	private  LiscomppptalesdControladorUrlEnum(String key, String value) {
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
