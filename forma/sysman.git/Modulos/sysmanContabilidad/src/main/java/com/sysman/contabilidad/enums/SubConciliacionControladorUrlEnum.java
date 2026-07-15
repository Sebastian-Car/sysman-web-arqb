/*
* SubConciliacionControladorUrlEnum
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
public enum SubConciliacionControladorUrlEnum {
   
           	URL9045("SUBCONCILIACIONCONTROLADORURL9045","39022"), 
           	URL9050("SUBCONCILIACIONCONTROLADORURL9050","39031"),
           	URL9545("SUBCONCILIACIONCONTROLADORURL9545","7009"),
           	URL15084("SUBCONCILIACIONCONTROLADORURL9045","39028"),
             	URL6076("SUBCONCILIACIONCONTROLADORURL6076","15019"),  
             	URL8222("SUBCONCILIACIONCONTROLADORURL8222","14042"),  
             	URL15847("SUBCONCILIACIONCONTROLADORURL15847","216001"),  
             	URL20100("SUBCONCILIACIONCONTROLADORURL20100","67001"),  
             	URL6368("SUBCONCILIACIONCONTROLADORURL6368","39020");
        	
	private final String key;
	private final String value;
	
	private  SubConciliacionControladorUrlEnum(String key, String value) {
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
