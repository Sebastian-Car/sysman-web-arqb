/*
* ListercerosControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ListercerosControladorUrlEnum {
   
           	URL5352("LISTERCEROSCONTROLADORURL5352","14012"),  
             	URL3333("LISTERCEROSCONTROLADORURL3333","14008"),  
             	URL4344("LISTERCEROSCONTROLADORURL4344","14010"),  
             	URL2417("LISTERCEROSCONTROLADORURL2417","14006");
        	
	private final String key;
	private final String value;
	
	private  ListercerosControladorUrlEnum(String key, String value) {
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
