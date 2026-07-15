/*
* JsonEnum
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.exc.kernel.api.commons.util.enums;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar los elementos raiz y base de los objetos JSON usados.
 */
public enum JsonEnum {

	DEFAULT("elementos","elemento");
	
	private final String base;
	private final String container;
	
	private JsonEnum(String base, String container) {
		this.base       = base;
		this.container = container;
	}

	public String getBase() {
		return base;
	}

	public String getContainer() {
		return container;
	}
}
