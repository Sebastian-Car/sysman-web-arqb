/*
 * MantenimientoVehiculosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum MantenimientoVehiculosControladorUrlEnum {

	URL507011("MANTENIMIENTOVEHICULOSCONTROLADORURL507011", "507011"), // Elemento Inicial (Activo)

	URL507013("MANTENIMIENTOVEHICULOSCONTROLADORURL507013", "507013"), // Elemento Final (Activo)

	URL507015("MANTENIMIENTOVEHICULOSCONTROLADORURL507015", "507015"), // Serie Inicial

	URL507017("MANTENIMIENTOVEHICULOSCONTROLADORURL507017", "507017"); // serie Final

	private final String key;
	private final String value;

	private MantenimientoVehiculosControladorUrlEnum(String key, String value) {
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
