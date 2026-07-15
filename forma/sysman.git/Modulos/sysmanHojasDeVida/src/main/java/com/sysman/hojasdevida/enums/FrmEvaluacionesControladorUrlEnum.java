/*
* FrmEvaluacionesControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmEvaluacionesControladorUrlEnum {
   
           	URL4812("FRMEVALUACIONESCONTROLADORURL4812","943001"),  
             	URL5453("FRMEVALUACIONESCONTROLADORURL5453","210101"),
             	URL8571("FRMEVALUACIONESCONTROLADORURL8571","934001");
        	
	private final String key;
	private final String value;
	
	private  FrmEvaluacionesControladorUrlEnum(String key, String value) {
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
