/*
* TraerPartidasConciliatoriasControladorUrlEnum
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
public enum TraerPartidasConciliatoriasControladorUrlEnum {
   
           	URL3251("TRAERPARTIDASCONCILIATORIASCONTROLADORURL3251","4001"),  
             	
           	URL3650("TRAERPARTIDASCONCILIATORIASCONTROLADORURL3650","70001"),  
            
           	URL8252("TRAERPARTIDASCONCILIATORIASCONTROLADORURL8252","70002");
        	
	private final String key;
	private final String value;
	
	private  TraerPartidasConciliatoriasControladorUrlEnum(String key, String value) {
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
