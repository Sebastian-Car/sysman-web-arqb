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
public enum DevolutivosenentregaControladorUrlEnum {

	URL001("DEVOLUTIVOSENENTREGACONTROLADORURL001", "112061"), 
	URL002("DEVOLUTIVOSENENTREGACONTROLADORURL002", "112063") 
	;

	private final String key;
	private final String value;

	private DevolutivosenentregaControladorUrlEnum(String key, String value) {
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
