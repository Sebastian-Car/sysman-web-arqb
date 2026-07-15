/**
 * 
 */
package com.sysman.plandesarrollo.enums;

/**
 *  Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * 
 * @version 1.0, 8/03/2018
 * @author dcastiblanco
 *
 */
public enum FrmseguimientoplandesarrolloControladorUrlEnum {

	URL001("FRMSEGUIMIENTOPLANDESARROLLOCONTROLADORURL", "4002");
	

	private final String key;
	private final String value;

	private FrmseguimientoplandesarrolloControladorUrlEnum(String key, String value) {
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