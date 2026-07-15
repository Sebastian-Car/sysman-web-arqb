/*
* BoletinDiariodeCajayBancosControladorUrlEnum
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
public enum BoletinDiariodeCajayBancosControladorUrlEnum {
   
           	URL3784("BOLETINDIARIODECAJAYBANCOSCONTROLADORURL3784","29015"),  
             	URL4820("BOLETINDIARIODECAJAYBANCOSCONTROLADORURL4820","29017");
        	
	private final String key;
	private final String value;
	
	private  BoletinDiariodeCajayBancosControladorUrlEnum(String key, String value) {
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
