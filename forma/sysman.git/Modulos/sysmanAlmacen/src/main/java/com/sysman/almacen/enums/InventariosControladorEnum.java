/*
* InventariosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum InventariosControladorEnum {
    PARAM2("TIPO"),
    PARAM1("CUENTAACT"),         
    PARAM0("PREDECESOR");
        	
	private final String value;
	
	private  InventariosControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
