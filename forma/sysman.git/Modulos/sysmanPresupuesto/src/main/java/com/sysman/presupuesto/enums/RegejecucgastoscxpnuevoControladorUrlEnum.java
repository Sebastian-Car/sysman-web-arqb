/*
* RegejecucgastoscxpnuevoControladorUrlEnum
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
public enum RegejecucgastoscxpnuevoControladorUrlEnum {
   
           	URL5037("REGEJECUCGASTOSCXPNUEVOCONTROLADORURL5037","94062"),  
             	URL4506("REGEJECUCGASTOSCXPNUEVOCONTROLADORURL4506","7001"),  
             	URL4104("REGEJECUCGASTOSCXPNUEVOCONTROLADORURL4104","4001"),  
             	URL6062("REGEJECUCGASTOSCXPNUEVOCONTROLADORURL6062","94064");
        	
	private final String key;
	private final String value;
	
	private  RegejecucgastoscxpnuevoControladorUrlEnum(String key, String value) {
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
