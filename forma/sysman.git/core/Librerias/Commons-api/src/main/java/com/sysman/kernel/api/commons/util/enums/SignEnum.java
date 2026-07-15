/*
* SignEnum
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.api.commons.util.enums;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar los diferentes signos usados en la libreria.
 */ 
public enum SignEnum { 
	 
	SPECIAL_BACKSLASH_S("\\\\s"),
	QUESTION("?"),
	AMPERSAND("&"),
	EQUAL("="),
	DOUBLE_QUOTES("\""),
	TWO_POINTS(":"),
	SPACE(" "),
	START_KEY("{"),
	END_KEY("}"),
	COMMA(","),
	POINT("."),
	EMPTY(""),
	SPECIAL_ADD("\\+"),
	SPECIAL_BACKSLASH("\\\\"),
	SPECIAL_BACKSLASH_N("\n"),
	SPECIAL_BACKSLASH_T("\t"),
	SPECIAL_BACKSLASH_R("\r");
	
	private final String value;
	
	private SignEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
