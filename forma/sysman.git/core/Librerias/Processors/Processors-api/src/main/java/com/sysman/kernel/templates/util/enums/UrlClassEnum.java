/*
* UrlClassEnum
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
 * Enumeracion que permite clasificar los parametros que
 * contendra la plantilla generadora de UrlClassEnum.
 */ 
public enum UrlClassEnum {
	
	DEFAULT_URL_ENUM("UrlEnum"),
	PACKAGE("package"),
	BASE_CLASS_NAME("baseClassName"),
	CLASS_NAME("className"),
	PARAM_NAME("param"),
	PARAM_LIST("paramList"),
	TEMPLATE("urlEnum"),
	URL_ID("url");
	
	private final String value;
	
	private UrlClassEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
