/*
* ExistenciadevxdepccControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum InformeComponentesControladorUrlEnum {
   
           	URL3238("INFORMECOMPONENTECONTROLADORURL3238","104050"),  
           	
             	URL4991("INFORMECOMPONENTECONTROLADORURL4991","1714001"),
             	
             	URL4055("INFORMECOMPONENTECONTROLADORURL4055","135005"),
             	
             	URL5683("INFORMECOMPONENTECONTROLADORURL5683","61018"),
             	
             	URL5684("INFORMECOMPONENTECONTROLADORURL5684","62002"),
             	
             	URL5685("INFORMECOMPONENTECONTROLADORURL5685","1718001")
             	
             	;
        	
	private final String key;
	private final String value;
	
	private  InformeComponentesControladorUrlEnum(String key, String value) {
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
