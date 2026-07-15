/*
* RevisarcuentaalmacensControladorUrlEnum
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
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum RevisarcuentaalmacensControladorUrlEnum {

	URL4905("REVISARCUENTAALMACENSCONTROLADORURL4905", "159003"),

	URL4369("REVISARCUENTAALMACENSCONTROLADORURL4369", "59009"),

	URL4589("REVISARCUENTAALMACENSCONTROLADORURL4589", "141080"),

	URL7157("REVISARCUENTAALMACENSCONTROLADORURL7157", "169001");

	private final String key;
	private final String value;

	private RevisarcuentaalmacensControladorUrlEnum(String key, String value) {
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
