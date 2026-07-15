/*
* TokenEnum
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.api.clientwso2.util.enums;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar los elementos del Java Beans Token.
 */  
public enum TokenEnum {

	ACCESS_TOKEN("access_token"),
	EXPIRES_IN("expires_in"),
	REFRESH_TOKEN("refresh_token"),
	TOKEN_TYPE("token_type");
	
	private String name;
	
	private TokenEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}