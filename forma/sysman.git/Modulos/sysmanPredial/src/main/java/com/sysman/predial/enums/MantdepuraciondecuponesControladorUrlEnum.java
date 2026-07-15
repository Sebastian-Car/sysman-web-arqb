/*
* MantdepuraciondecuponesControladorUrlEnum
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
public enum MantdepuraciondecuponesControladorUrlEnum {
   
           	URL8749("MANTDEPURACIONDECUPONESCONTROLADORURL8749","408004"),  
             	URL7848("MANTDEPURACIONDECUPONESCONTROLADORURL7848","408003"),  
             	URL6262("MANTDEPURACIONDECUPONESCONTROLADORURL6262","408001"),  
             	URL9618("MANTDEPURACIONDECUPONESCONTROLADORURL9618","408005"),  
             	URL10969("MANTDEPURACIONDECUPONESCONTROLADORURL10969","408007"),  
             	URL7087("MANTDEPURACIONDECUPONESCONTROLADORURL7087","408002");
        	
	private final String key;
	private final String value;
	
	private  MantdepuraciondecuponesControladorUrlEnum(String key, String value) {
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
