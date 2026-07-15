/**
 * 
 */
package com.sysman.general.enums;

/**
 * 
 */
public enum FrmFormuladorNominaControladorEnum {

	ID_DE_FACTOR("ID_DE_FACTOR"), 
	
	PROCEDIMIENTO("PROCEDIMIENTO"),
	
	ID_DE_CONCEPTO("ID_DE_CONCEPTO"),
	
	NOMBRE_CONCEPTO("NOMBRE_CONCEPTO"),
	
	ALIAS_CONCEPTO("ALIAS_CONCEPTO"), 
	
	ACUMULADO("ACUMULADO"), 
	
	PERIODICIDAD("PERIODICIDAD"), 
	
	DOCEAVA("DOCEAVA"), 
	
	TIPO_DOCEAVA("TIPO_DOCEAVA"), 
	
	CONCEPTO_RESULTANTE("CONCEPTO_RESULTANTE"),
	
	;

	private final String value;

	private FrmFormuladorNominaControladorEnum(String value) {
		  this.value = value;
		}

	public String getValue() {
		return value;
	}
}
