/*
 * EstratosfgControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeración que permite clasificar cada uno de los identificadores
 *          generados en el refactoring y asociados al código legacy obtenido
 *          con patrones de búsqueda.
 */
public enum ConfcuentasbancariasControladorUrlEnum {
	URL161("CONFCUENTASBANCARIASCONTROLADORURL161", "4001");

	private final String key;
	private final String value;

	private ConfcuentasbancariasControladorUrlEnum(String key, String value) {
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
