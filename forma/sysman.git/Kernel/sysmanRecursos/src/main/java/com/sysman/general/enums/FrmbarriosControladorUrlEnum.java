/*
* FrmbarriosControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmbarriosControladorUrlEnum {
   
           	URL5988("FRMBARRIOSCONTROLADORURL5988","97001"),  
             	URL4261("FRMBARRIOSCONTROLADORURL4261","1001"),  
             	URL6466("FRMBARRIOSCONTROLADORURL6466","99001"),  
             	URL4717("FRMBARRIOSCONTROLADORURL4717","2001"),  
             	URL5288("FRMBARRIOSCONTROLADORURL5288","5002");
        	
	private final String key;
	private final String value;
	
	private  FrmbarriosControladorUrlEnum(String key, String value) {
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
