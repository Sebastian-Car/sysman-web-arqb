/*
* TemplateTypeEnum
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
 * Enumeracion que permite clasificar elementos de envio a servicios. Parametros, url, etc.
 */ 
public enum TemplateTypeEnum {
 
	PARAMETER("parameter"),
	URL("url");
	
	private final String value;
	
	private TemplateTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
