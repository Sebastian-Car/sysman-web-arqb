/*
 * ApropiacioninicialanoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ElementospermanbodegaControladorUrlEnum {

	URL001("ELEMENTOSPERMANBODEGACONTROLADORURL001", "112143"), 
	URL002("ELEMENTOSPERMANBODEGACONTROLADORURL002", "112145")
	;

	private final String key;
	private final String value;

	private ElementospermanbodegaControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
