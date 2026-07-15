/*
* StructureEnum
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
 * Enumeracion que contiene el listado de todos los elementos de una plantilla base PL-SQL, WS, etc.
 */
public enum StructureEnum {
	
	PL_SQL("package","method");
	
	private final String pack;
	private final String method;
	
	private StructureEnum(String pack, String method) {
		this.pack   = pack;
		this.method = method;
	}

	public String getPack() {
		return pack;
	}

	public String getMethod() {
		return method;
	}
}
