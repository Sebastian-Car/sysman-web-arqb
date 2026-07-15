/*
* BalanceAperturaNiifMgcUrlEnum
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
public enum BalanceAperturaNiifMgcUrlEnum {
   
	URL6292("BALANCEAPERTURANIIFMGCURL6292","16005"),        	
           	URL7317("BALANCEAPERTURANIIFMGCURL7317","16003");
	private final String key;
	private final String value;
	
	private  BalanceAperturaNiifMgcUrlEnum(String key, String value) {
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
