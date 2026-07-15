/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author mrosero
*Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FmrElementossinsaliraservicioControladorUrlEnum {
	
	URL1229("FMRELEMENTOSSINSALIRASERVICIOCONTROLADORURL1229","112172"),//PAGINADO
	URL1230("FMRELEMENTOSSINSALIRASERVICIOCONTROLADORURL1230","112174");
	
	private final String key;
	private final String value;

	private FmrElementossinsaliraservicioControladorUrlEnum(String key, String value) {
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
