/*
* FrmasignaporcentajereservaControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmasignaporcentajereservaControladorUrlEnum {
   
           	URL4111("FRMASIGNAPORCENTAJERESERVACONTROLADORURL4111","367045"),  
             	URL5123("FRMASIGNAPORCENTAJERESERVACONTROLADORURL5123","367046"),
             	URL4657("FRMASIGNAPORCENTAJERESERVACONTROLADORURL4657","367048");
        	
	private final String key;
	private final String value;
	
	private  FrmasignaporcentajereservaControladorUrlEnum(String key, String value) {
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
