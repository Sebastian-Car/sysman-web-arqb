/*
* LmaestrousuariosControladorUrlEnum
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
public enum LmaestrousuariosControladorUrlEnum {
   
           	URL7530("LMAESTROUSUARIOSCONTROLADORURL7530","242003"),  
             	URL6830("LMAESTROUSUARIOSCONTROLADORURL6830","242001"),  
             	URL5176("LMAESTROUSUARIOSCONTROLADORURL5176","310010"),  
             	URL5889("LMAESTROUSUARIOSCONTROLADORURL5889","214068"),  
             	URL4627("LMAESTROUSUARIOSCONTROLADORURL4627","310009");
        	
	private final String key;
	private final String value;
	
	private  LmaestrousuariosControladorUrlEnum(String key, String value) {
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
