/*
 * FormularioIntegradoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum FormularioIntegradoControladorUrlEnum {

	URL22663("FORMULARIOINTEGRADOCONTROLADORURL22663", "471030"),

	URL22108("FORMULARIOINTEGRADOCONTROLADORURL22108", "4001"),

	URL9097("FORMULARIOINTEGRADOCONTROLADORURL9097", "471031"),

	URL9098("FORMULARIOINTEGRADOCONTROLADORURLURL9098", "118023"),

	URL9099("FORMULARIOINTEGRADOCONTROLADORURLURL9099", "944002"),

	URL9096("FORMULARIOINTEGRADOCONTROLADORURLURL9096", "950001"),

	URL9095("HISTORICOSCONTROLADORURL6890", "210043"),

	URL9094("HISTORICOSCONTROLADORURL6890", "1012001"),

	URL9093("FORMULARIOINTEGRADOCONTROLADORURL9093", "471074");

	private final String key;
	private final String value;

	private FormularioIntegradoControladorUrlEnum(String key, String value) {
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
