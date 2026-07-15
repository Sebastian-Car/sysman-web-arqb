/**
 * 
 */
package com.sysman.presupuesto.enums;

/**
 * @author dcastiblanco
 *
 */
public enum FrejecucioninversionControladorEnum {

	PARAM0("CUENTAINICIAL"),

	PARAM1("CODIGOINICIAL"),
	
	PARAM2("REFERENCIAINICIAL"),
	
	PARAM3("AUXILIARINICIAL");

	private final String value;

	private FrejecucioninversionControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
