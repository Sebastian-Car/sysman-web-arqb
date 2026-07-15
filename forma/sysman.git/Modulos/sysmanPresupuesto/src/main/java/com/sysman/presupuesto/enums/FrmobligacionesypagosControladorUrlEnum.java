/*
* FrmobligacionesypagosControladorUrlEnum
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
public enum FrmobligacionesypagosControladorUrlEnum {
   
           	URL5293("FRMOBLIGACIONESYPAGOSCONTROLADORURL5293","94054"),  
             	URL3752("FRMOBLIGACIONESYPAGOSCONTROLADORURL3752","4002"),  
             	URL4177("FRMOBLIGACIONESYPAGOSCONTROLADORURL4177","94052");
        	
	private final String key;
	private final String value;
	
	private  FrmobligacionesypagosControladorUrlEnum(String key, String value) {
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
