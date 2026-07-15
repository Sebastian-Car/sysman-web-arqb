/*
* CuentabancosControladorUrlEnum
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
public enum CuentabancosControladorUrlEnum {
   
           	URL3433("CUENTABANCOSCONTROLADORURL3433","36001"),  
             	URL3923("CUENTABANCOSCONTROLADORURL3923","34001"),  
             	URL5251("CUENTABANCOSCONTROLADORURL5251","5500C"),
                URL5252("CUENTABANCOSCONTROLADORURL5251","55001");
        	
	private final String key;
	private final String value;
	
	private  CuentabancosControladorUrlEnum(String key, String value) {
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
