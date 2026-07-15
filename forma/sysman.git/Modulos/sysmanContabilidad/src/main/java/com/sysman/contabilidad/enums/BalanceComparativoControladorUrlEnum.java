/*
* BalanceComparativoControladorUrlEnum
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
public enum BalanceComparativoControladorUrlEnum {
   
           	URL4579("BALANCECOMPARATIVOCONTROLADORURL4579","4001"), 
           	
             	URL5464("BALANCECOMPARATIVOCONTROLADORURL5464","29019"),  
             	
             	URL6237("BALANCECOMPARATIVOCONTROLADORURL6237","29021");
        	
	private final String key;
	private final String value;
	
	private  BalanceComparativoControladorUrlEnum(String key, String value) {
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
