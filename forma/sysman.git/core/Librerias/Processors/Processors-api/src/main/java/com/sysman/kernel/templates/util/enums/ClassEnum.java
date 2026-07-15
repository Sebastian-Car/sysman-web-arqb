/*
* ClassEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.util.enums;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los elementos 
 * que se encuentran o puede contener un enum Java.
 */  
public enum ClassEnum {
	
	DEFAULT_ENUM("Enum"),
	PACKAGE("package"),
	CLASS_NAME("className"),
	PARAM_NAME("param"),
	PARAM_LIST("paramList"),
	TEMPLATE("enum");
	
	private final String value;
	
	private ClassEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
