/*
 * BoletinTesoreriaDepControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum BoletinTesoreriaDepControladorUrlEnum {

	URL7046("BOLETINTESORERIADEPCONTROLADORURL7046", "7046"),

	URL7045("BOLETINTESORERIADEPCONTROLADORURL7045", "7045"),

	URL4001("BOLETINTESORERIADEPCONTROLADORURL4001", "4001"),

	URL16178("BOLETINTESORERIADEPCONTROLADORURL16178", "16178"),

	URL16176("BOLETINTESORERIADEPCONTROLADORURL16176", "16176");

	private final String key;
	private final String value;

	private BoletinTesoreriaDepControladorUrlEnum(String key, String value) {
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
