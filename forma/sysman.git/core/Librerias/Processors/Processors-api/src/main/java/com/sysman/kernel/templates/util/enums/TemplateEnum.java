/*
* TemplateEnum
*
* 1.0
*
* 30/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.util.enums;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los 
 * nombres de properties existentes en la libreria.
 */  
public enum TemplateEnum {
	
	ENUM("enum"),
	URL_ENUM("urlEnum"),
	SERVICE("service");
	
	private final String value;
	
	private TemplateEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
