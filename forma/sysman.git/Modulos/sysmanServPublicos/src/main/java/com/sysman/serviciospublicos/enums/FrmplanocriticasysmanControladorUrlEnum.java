/*
* FrmplanocriticasysmanControladorUrlEnum
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
public enum FrmplanocriticasysmanControladorUrlEnum {
   
           	URL7622("FRMPLANOCRITICASYSMANCONTROLADORURL7622","214053"),
           	URL4783("FRMPLANOCRITICASYSMANCONTROLADORURL4783","213083"),
           	URL4721("FRMPLANOCRITICASYSMANCONTROLADORURL4721","213085"),
           	URL5841("FRMPLANOCRITICASYSMANCONTROLADORURL5841","362003");
        	
	private final String key;
	private final String value;
	
	private  FrmplanocriticasysmanControladorUrlEnum(String key, String value) {
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
