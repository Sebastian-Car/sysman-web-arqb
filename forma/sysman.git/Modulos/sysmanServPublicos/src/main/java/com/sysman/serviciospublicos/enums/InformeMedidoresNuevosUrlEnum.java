/*
* InformeMedidoresNuevosUrlEnum
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
public enum InformeMedidoresNuevosUrlEnum {
   
           	URL7241("INFORMEMEDIDORESNUEVOSURL7241","213126"),  
             	URL6690("INFORMEMEDIDORESNUEVOSURL6690","214031"),  
             	URL7835("INFORMEMEDIDORESNUEVOSURL7835","213128"),  
             	URL8428("INFORMEMEDIDORESNUEVOSURL8428","227025"),
             	URL8430("INFORMEMEDIDORESNUEVOSURL8430","227060");
        	
	private final String key;
	private final String value;
	
	private  InformeMedidoresNuevosUrlEnum(String key, String value) {
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
