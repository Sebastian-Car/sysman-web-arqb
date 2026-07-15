/*
* LisRegistrosAbiertosCuentasControladorUrlEnum
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
public enum LisRegistrosAbiertosCuentasControladorUrlEnum {
   
           	URL6020("LISREGISTROSABIERTOSCUENTASCONTROLADORURL6020","13008"),  
             	URL6897("LISREGISTROSABIERTOSCUENTASCONTROLADORURL6897","13012"),  
             	URL3829("LISREGISTROSABIERTOSCUENTASCONTROLADORURL3829","29098"),  
             	URL4888("LISREGISTROSABIERTOSCUENTASCONTROLADORURL4888","29100"),  
             	URL7549("LISREGISTROSABIERTOSCUENTASCONTROLADORURL7549","14001"),  
             	URL8027("LISREGISTROSABIERTOSCUENTASCONTROLADORURL8027","14026");
        	
	private final String key;
	private final String value;
	
	private  LisRegistrosAbiertosCuentasControladorUrlEnum(String key, String value) {
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
