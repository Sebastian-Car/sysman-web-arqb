/*
* FrmvigenciapresupuestalgastosduControladorUrlEnum
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
public enum FrmvigenciapresupuestalgastosduControladorUrlEnum {
   
           	URL6933("FRMVIGENCIAPRESUPUESTALGASTOSDUCONTROLADORURL6933","29088"),  
             	URL4965("FRMVIGENCIAPRESUPUESTALGASTOSDUCONTROLADORURL4965","4002"),  
             	URL5973("FRMVIGENCIAPRESUPUESTALGASTOSDUCONTROLADORURL5973","29086"),  
             	URL5445("FRMVIGENCIAPRESUPUESTALGASTOSDUCONTROLADORURL5445","7007");
        	
	private final String key;
	private final String value;
	
	private  FrmvigenciapresupuestalgastosduControladorUrlEnum(String key, String value) {
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
