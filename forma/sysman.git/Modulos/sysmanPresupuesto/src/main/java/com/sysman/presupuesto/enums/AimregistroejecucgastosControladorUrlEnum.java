/*
* AimregistroejecucgastosControladorUrlEnum
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
public enum AimregistroejecucgastosControladorUrlEnum {
   
           	URL6411("AIMREGISTROEJECUCGASTOSCONTROLADORURL6411","94036"),  
             	URL7704("AIMREGISTROEJECUCGASTOSCONTROLADORURL7704","94034"),  
             	URL4897("AIMREGISTROEJECUCGASTOSCONTROLADORURL4897","7016"),  
             	URL5604("AIMREGISTROEJECUCGASTOSCONTROLADORURL5604","4007");
        	
	private final String key;
	private final String value;
	
	private  AimregistroejecucgastosControladorUrlEnum(String key, String value) {
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
