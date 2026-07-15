/*
* EgrycomafectadosControladorUrlEnum
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
public enum EgrycomafectadosControladorUrlEnum {
   
           	URL4366("EGRYCOMAFECTADOSCONTROLADORURL4366","15012"),
           	
             	URL3246("EGRYCOMAFECTADOSCONTROLADORURL3246","72009"),  
             	
             	URL2772("EGRYCOMAFECTADOSCONTROLADORURL2772","4002");
        	
	private final String key;
	private final String value;
	
	private  EgrycomafectadosControladorUrlEnum(String key, String value) {
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
