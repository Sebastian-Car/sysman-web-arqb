/*
* AuxPptalReferenciaControladorUrlEnum
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
public enum AuxPptalReferenciaControladorUrlEnum {
   
           	URL8028("AUXPPTALREFERENCIACONTROLADORURL8028","94008"),  
             	URL3663("AUXPPTALREFERENCIACONTROLADORURL3663","25008"),  
             	URL4414("AUXPPTALREFERENCIACONTROLADORURL4414","25012"),  
             	URL6666("AUXPPTALREFERENCIACONTROLADORURL6666","13005"),  
             	URL5487("AUXPPTALREFERENCIACONTROLADORURL5487","13003"),  
             	URL9056("AUXPPTALREFERENCIACONTROLADORURL9056","94010");
        	
	private final String key;
	private final String value;
	
	private  AuxPptalReferenciaControladorUrlEnum(String key, String value) {
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
