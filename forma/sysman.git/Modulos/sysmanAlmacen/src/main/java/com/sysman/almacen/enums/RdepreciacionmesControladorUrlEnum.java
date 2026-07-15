/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author dcastiblanco
 *
 */
public enum RdepreciacionmesControladorUrlEnum {
	
	URL5600("RDEPRECIACIONMESCONTROLADORURL5600", "4001"),
	
	URL5230("RDEPRECIACIONMESCONTROLADORURL230", "7001"),
	
	URL11959("RDEPRECIACIONMESCONTROLADORURL11959","112032"),
	
	URL2591("RDEPRECIACIONMESCONTROLADORURL2591", "141056"),

	URL2901("RDEPRECIACIONMESCONTROLADORURL2901", "141058"),
	
	URL12000("MOVIMIENTOELEMENTOCONTROLADORURL12000","112034");
	
	private final String key;
	private final String value;

	private RdepreciacionmesControladorUrlEnum(String key, String value) {
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