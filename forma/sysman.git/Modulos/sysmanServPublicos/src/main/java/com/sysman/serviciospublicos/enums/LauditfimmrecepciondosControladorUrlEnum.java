/*
* LauditfimmrecepciondosControladorUrlEnum
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
public enum LauditfimmrecepciondosControladorUrlEnum {
   
           	URL6965("LAUDITFIMMRECEPCIONDOSCONTROLADORURL6965","214027"),  
             	URL8473("LAUDITFIMMRECEPCIONDOSCONTROLADORURL8473","213132"),  
             	URL6533("LAUDITFIMMRECEPCIONDOSCONTROLADORURL6533","214026"),  
             	URL9463("LAUDITFIMMRECEPCIONDOSCONTROLADORURL9463","362005"),  
             	URL7481("LAUDITFIMMRECEPCIONDOSCONTROLADORURL7481","213130");
        	
	private final String key;
	private final String value;
	
	private  LauditfimmrecepciondosControladorUrlEnum(String key, String value) {
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
