/*
* ProgramacionpagosControladorUrlEnum
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
public enum ProgramacionpagosControladorUrlEnum {
   
           	URL6547("PROGRAMACIONPAGOSCONTROLADORURL6547","72011"),
           	URL1023("PROGRAMACIONPAGOSCONTROLADORURL1023","72013"),
           	URL7225("PROGRAMACIONPAGOSCONTROLADORURL7225","72015"),
           	URL426("PROGRAMACIONPAGOSCONTROLADORURL426","4001"),
           	URL72116("PROGRAMACIONPAGOSCONTROLADORURL72116","72116");
        	
	private final String key;
	private final String value;
	
	private  ProgramacionpagosControladorUrlEnum(String key, String value) {
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
