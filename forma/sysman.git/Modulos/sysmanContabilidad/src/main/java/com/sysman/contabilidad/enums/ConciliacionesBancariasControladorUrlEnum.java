/*
 * ConciliacionesBancariasControladorUrlEnum
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

public enum ConciliacionesBancariasControladorUrlEnum {
	URL001("VERPATIDASCONCILIATORIASCONTROLADOR001", "4001"),
	URL002("VERPATIDASCONCILIATORIASCONTROLADOR002", "7001"), 
	URL003("VERPATIDASCONCILIATORIASCONTROLADOR003", "29150"),
	URL204("PERIODOCONCILIACIONCONTROLADORURL204", "4011");
	
	private final String key;
	private final String value;

	private ConciliacionesBancariasControladorUrlEnum(String key, String value) {
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
