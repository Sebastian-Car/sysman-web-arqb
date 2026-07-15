/*
* CambiosPatrimoniosControladorUrlEnum
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
public enum CambiosPatrimoniosControladorUrlEnum {
   
           	URL8327("CAMBIOSPATRIMONIOSCONTROLADORURL8327","16005"),
           	
             	URL5904("CAMBIOSPATRIMONIOSCONTROLADORURL5904","16012"),  
             	
             	URL7518("CAMBIOSPATRIMONIOSCONTROLADORURL7518","16012"),  
             	
             	URL6715("CAMBIOSPATRIMONIOSCONTROLADORURL6715","16005"),  
             	 
             	URL4134("CAMBIOSPATRIMONIOSCONTROLADORURL4134","4001"), 
             	
             	URL4471("CAMBIOSPATRIMONIOSCONTROLADORURL4471","7005");
        	
	private final String key;
	private final String value;
	
	private  CambiosPatrimoniosControladorUrlEnum(String key, String value) {
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
