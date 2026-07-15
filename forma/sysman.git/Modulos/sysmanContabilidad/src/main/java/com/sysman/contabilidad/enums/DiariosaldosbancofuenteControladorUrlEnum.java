/*
* DiariosaldosbancofuenteControladorUrlEnum
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
public enum DiariosaldosbancofuenteControladorUrlEnum {
   
           	URL4655("DIARIOSALDOSBANCOFUENTECONTROLADORURL4655","36004"),  
             	URL3967("DIARIOSALDOSBANCOFUENTECONTROLADORURL3967","36002"),  
             	URL6313("DIARIOSALDOSBANCOFUENTECONTROLADORURL6313","34003"),  
             	URL5389("DIARIOSALDOSBANCOFUENTECONTROLADORURL5389","34001");
        	
	private final String key;
	private final String value;
	
	private  DiariosaldosbancofuenteControladorUrlEnum(String key, String value) {
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
