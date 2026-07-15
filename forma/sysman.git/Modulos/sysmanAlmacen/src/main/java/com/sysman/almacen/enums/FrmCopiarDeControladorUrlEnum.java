/*
* AfectacioncontratosControladorUrlEnum
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
public enum FrmCopiarDeControladorUrlEnum {
   
        URL79165("AFECTACIONCONTRATOSCONTROLADORURL79165","139029"),
	
        URL52478("AFECTACIONCONTRATOSCONTROLADORURL52478","41012");
        	
	private final String key;
	private final String value;
	
	private  FrmCopiarDeControladorUrlEnum(String key, String value) {
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
