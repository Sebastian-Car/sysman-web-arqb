/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author dcastiblanco
 *Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ExistenciaBodegaControladorUrlEnum {
	URL5600("EXISTENCIABODEGACONTROLADORURL5600", "4001"),
	URL5230("EXISTENCIABODEGACONTROLADORURL230", "7001"),
	URL11959("EXISTENCIABODEGACONTROLADORURL11959","112162"),
	URL12000("EXISTENCIABODEGACONTROLADORURL12000","112164");

	private final String key;
	private final String value;

	private ExistenciaBodegaControladorUrlEnum(String key, String value) {
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