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
public enum ConsumoControladoControladorUrlEnum {

	URL001("CONSUMOCONTROLADOCONTROLADORURL001", "62005"), 
	URL002("CONSUMOCONTROLADOCONTROLADORURL002", "62013"), //dependencia
	URL003("CONSUMOCONTROLADOCONTROLADORURL003", "61033")  //responsable
	;

	private final String key;
	private final String value;

	private ConsumoControladoControladorUrlEnum(String key, String value) {
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
