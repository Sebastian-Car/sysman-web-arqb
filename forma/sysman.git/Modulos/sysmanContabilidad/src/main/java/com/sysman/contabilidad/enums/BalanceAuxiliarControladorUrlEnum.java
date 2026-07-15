/*
* BalanceAuxiliarControladorUrlEnum
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
public enum BalanceAuxiliarControladorUrlEnum {
   
           	URL8425("BALANCEAUXILIARCONTROLADORURL8425","20021"),  
             	URL5715("BALANCEAUXILIARCONTROLADORURL5715","29009"), 
             	URL6839("BALANCEAUXILIARCONTROLADORURL6839","34043"),  //Auxiliar inicial //New fuente recursos inicial
             	URL7567("BALANCEAUXILIARCONTROLADORURL7567","34045"), //Auxiliar final // New fuente recursos final
             	URL4224("BALANCEAUXILIARCONTROLADORURL4224","4001"),  
             	URL4758("BALANCEAUXILIARCONTROLADORURL4758","29007");
        	
	private final String key;
	private final String value;
	
	private  BalanceAuxiliarControladorUrlEnum(String key, String value) {
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
