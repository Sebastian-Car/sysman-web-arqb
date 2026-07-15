/*
* SubMedicinaPrepagadaControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum SubMedicinaPrepagadaControladorUrlEnum {
   
           	URL4592("SUBMEDICINAPREPAGADACONTROLADORURL4592","638008"),
           	URL15084("SUBMEDICINAPREPAGADACONTROLADORURL15084","685041"),
           	URL9050("SUBMEDICINAPREPAGADACONTROLADORURL4592","685043");
        	
	private final String key;
	private final String value;
	
	private  SubMedicinaPrepagadaControladorUrlEnum(String key, String value) {
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
