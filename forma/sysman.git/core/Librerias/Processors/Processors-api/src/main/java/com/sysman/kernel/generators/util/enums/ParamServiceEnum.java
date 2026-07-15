/*
* ParamServiceEnum
*
* 1.0
*
* 30/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.util.enums;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * usados en la plantilla de servicios
 */  
public enum ParamServiceEnum {
	
	CLASS_NAME("className"),
	PACKAGE_NAME("package"),
	PARAM_GET_SERVICE("paramGetList"),
	PARAM_POST_SERVICE("paramPostList"),
	PARAM_PUT_SERVICE("paramPutList"),
	PARAM_DELETE_SERVICE("paramDeleteList");
	
	private final String value;

	private ParamServiceEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
