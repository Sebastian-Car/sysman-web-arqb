/**
 * 
 */
package com.sysman.plandesarrollo.enums;

/**
 * Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * @author dcastiblanco
 *
 */
public enum FrseguiminetocuatrenioControladorUrlEnum {
	
URL001("FRMSEGUIMIENTOPLANDESARROLLOCONTROLADORURL", "4002");
	

	private final String key;
	private final String value;

	private FrseguiminetocuatrenioControladorUrlEnum(String key, String value) {
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