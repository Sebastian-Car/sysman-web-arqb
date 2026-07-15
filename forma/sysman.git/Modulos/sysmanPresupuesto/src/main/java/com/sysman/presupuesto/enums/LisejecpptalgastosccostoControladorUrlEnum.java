/*
* LisejecpptalgastosccostoControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum LisejecpptalgastosccostoControladorUrlEnum {
   
             	URL8934("LISEJECPPTALGASTOSCCOSTOCONTROLADORURL8934","29090"),  
             	URL9915("LISEJECPPTALGASTOSCCOSTOCONTROLADORURL9915","29092"),  
             	URL11026("LISEJECPPTALGASTOSCCOSTOCONTROLADORURL11026","20013"),  
             	URL11696("LISEJECPPTALGASTOSCCOSTOCONTROLADORURL11696","20015"),  
             	URL8510("LISEJECPPTALGASTOSCCOSTOCONTROLADORURL8510","7007"),  
             	URL8140("LISEJECPPTALGASTOSCCOSTOCONTROLADORURL8140","4001");
        	
	private final String key;
	private final String value;
	
	private  LisejecpptalgastosccostoControladorUrlEnum(String key, String value) {
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
