/*
* RequisicionesPendientesControladorUrlEnum
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
public enum RequisicionesPendientesControladorUrlEnum {
   
           	URL5931("REQUISICIONESPENDIENTESCONTROLADORURL5931","62019"),  
             	URL4250("REQUISICIONESPENDIENTESCONTROLADORURL4250","112063"),  
             	URL5228("REQUISICIONESPENDIENTESCONTROLADORURL5228","62017"),  
             	URL3516("REQUISICIONESPENDIENTESCONTROLADORURL3516","112061");
        	
	private final String key;
	private final String value;
	
	private  RequisicionesPendientesControladorUrlEnum(String key, String value) {
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
