/*
* BeneficiariosupcsControladorUrlEnum
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
public enum BeneficiariosupcsControladorUrlEnum {
   
           	URL4837("BENEFICIARIOSUPCSCONTROLADORURL4837","209001"),  
             	URL5411("BENEFICIARIOSUPCSCONTROLADORURL5411","609001"),  
             	URL5875("BENEFICIARIOSUPCSCONTROLADORURL5875","210010");
        	
	private final String key;
	private final String value;
	
	private  BeneficiariosupcsControladorUrlEnum(String key, String value) {
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
