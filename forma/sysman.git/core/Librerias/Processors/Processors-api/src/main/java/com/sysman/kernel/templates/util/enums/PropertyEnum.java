/*
* PropertyEnum
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
 * Enumeracion que contiene el listado de todos los properties disponibles en la libreria.
 */ 
public enum PropertyEnum {
	
	GEN_PATTERNS("gen_patterns"),
	GEN_PARAMS("gen_params"),
	GEN_PARAM("gen_param"),
	PATTERNS("patterns"),
	PARAMS("parameters");
	
	private final String value;
	
	private PropertyEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
