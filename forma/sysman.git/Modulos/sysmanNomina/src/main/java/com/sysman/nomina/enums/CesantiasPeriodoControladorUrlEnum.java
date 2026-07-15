/*
* CesantiasPeriodoControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum CesantiasPeriodoControladorUrlEnum {
   
           	URL5201("CESANTIASPERIODOCONTROLADORURL5201","7024"),  
             	URL3926("CESANTIASPERIODOCONTROLADORURL3926","471001"),
             	URL3978("CESANTIASPERIODOCONTROLADORURL3978","471003"), 
             	URL6814("CESANTIASPERIODOCONTROLADORURL6814","471004"),  
             	URL4644("CESANTIASPERIODOCONTROLADORURL4644","471002");
        	
	private final String key;
	private final String value;
	
	private  CesantiasPeriodoControladorUrlEnum(String key, String value) {
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
