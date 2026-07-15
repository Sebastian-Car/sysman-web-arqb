/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author mrosero
 *
 */
public enum FmrElementossinsaliraservicioControladorEnum {

	ELEMENTO("ELEMENTO");

	private final String value;
	private FmrElementossinsaliraservicioControladorEnum(String a) {
		this.value=a;
	}
	public String getValue() {
		return value;
	}
}

