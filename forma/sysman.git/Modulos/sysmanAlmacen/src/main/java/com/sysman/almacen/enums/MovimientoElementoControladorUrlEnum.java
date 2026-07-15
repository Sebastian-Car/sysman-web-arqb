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
public enum MovimientoElementoControladorUrlEnum {
	URL11959("MOVIMIENTOELEMENTOCONTROLADORURL11959","112032"),
	
	URL12000("MOVIMIENTOELEMENTOCONTROLADORURL12000","112034");

	private final String key;
	private final String value;
	
	private  MovimientoElementoControladorUrlEnum(String key, String value) {
	    this.key   = key; 
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}