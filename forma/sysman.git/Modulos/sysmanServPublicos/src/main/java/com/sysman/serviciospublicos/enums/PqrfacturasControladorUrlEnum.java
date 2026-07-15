/*
* PqrfacturasControladorUrlEnum
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
public enum PqrfacturasControladorUrlEnum {
   
                URL21585("PQRFACTURASCONTROLADORURL21585","326002"),  
                URL18410("PQRFACTURASCONTROLADORURL18410","326006"),  
                URL22556("PQRFACTURASCONTROLADORURL22556","326005");

                private final String key;
	private final String value;
	
	private  PqrfacturasControladorUrlEnum(String key, String value) {
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
