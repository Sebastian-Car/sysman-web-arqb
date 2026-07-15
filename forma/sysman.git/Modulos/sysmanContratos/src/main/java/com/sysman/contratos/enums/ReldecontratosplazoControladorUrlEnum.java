/*
* ReldecontratosplazoControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ReldecontratosplazoControladorUrlEnum {
   
           	URL3545("RELDECONTRATOSPLAZOCONTROLADORURL3545","73031"), 
           	URL3574("RELDECONTRATOSPLAZOCONTROLADORURL3574","73032"),  
             	URL4447("RELDECONTRATOSPLAZOCONTROLADORURL4447","14067"),  
             	URL5087("RELDECONTRATOSPLAZOCONTROLADORURL5087","14090");
        	
	private final String key;
	private final String value;
	
	private  ReldecontratosplazoControladorUrlEnum(String key, String value) {
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
