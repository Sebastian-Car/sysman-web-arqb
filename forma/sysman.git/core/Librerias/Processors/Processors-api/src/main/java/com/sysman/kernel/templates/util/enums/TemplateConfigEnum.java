/*
* TemplateConfigEnum
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
 * Enumeracion que contiene informacion de contexto 
 * para las plantillas generadores de codigo
 */ 
public enum TemplateConfigEnum {
	RESOURCE_LOADER("resource.loader","class"),
	CLASS_RESOURCE_LOADER("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	
	private final String key;
	private final String value;
	
	private TemplateConfigEnum(String key, String value) {
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
