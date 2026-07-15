/*
* FrmrecaudostrimestralesControladorUrlEnum
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
public enum FrmrecaudostrimestralesControladorUrlEnum {
   
           	URL5258("FRMRECAUDOSTRIMESTRALESCONTROLADORURL5258","227001"), 
           	URL7622("FRMRECAUDOSTRIMESTRALESCONTROLADORURL7622","59010"),
             	URL4665("FRMRECAUDOSTRIMESTRALESCONTROLADORURL4665","214031");
        	
	private final String key;
	private final String value;
	
	private  FrmrecaudostrimestralesControladorUrlEnum(String key, String value) {
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
