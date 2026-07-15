/*
* ListaCierreControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum ListaCierreControladorEnum {
   
                PARAM0("PARAM0");
        	
	private final String value;
	
	private  ListaCierreControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
