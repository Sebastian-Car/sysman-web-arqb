/*
* ServiceEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.util.enums;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los tipos 
 * de peticion usados y anotados en las fuentes de datos para
 * la transformacion en metodos de servicios web
 */  
public enum ServiceEnum {
	
	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE");
	
	private final String value;

	private ServiceEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
