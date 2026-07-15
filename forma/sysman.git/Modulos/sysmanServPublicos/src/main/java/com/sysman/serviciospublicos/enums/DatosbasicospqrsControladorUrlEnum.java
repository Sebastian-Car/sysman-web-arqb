/*
* DatosbasicospqrsControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum DatosbasicospqrsControladorUrlEnum {
   
           	URL5575("DATOSBASICOSPQRSCONTROLADORURL5575","2001"),  
             	URL3655("DATOSBASICOSPQRSCONTROLADORURL3655","335004"), 
             	URL15084("DATOSBASICOSPQRSCONTROLADORURL15084","335001"),
             	URL9050("DATOSBASICOSPQRSCONTROLADORURL9050","335002"),
             	URL6969("DATOSBASICOSPQRSCONTROLADORURL6969","335003"),
             	URL5111("DATOSBASICOSPQRSCONTROLADORURL5111","5001");
        	
	private final String key;
	private final String value;
	
	private  DatosbasicospqrsControladorUrlEnum(String key, String value) {
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
