package com.sysman.rest.enums;
/**
 * Clase: EnumConstantesAuxiliar.java
 * 
 * enumerado para el manejo de constantes para las auxiliares
 * 
 * @version 1.0, 06/03/2019
 * @author José Pascual Gómez Blanco
 *  * 
 */



public enum EnumAuxiliaresVarios {
	
	TERCERO("999999999999999999"),
	
	SUCURSAL("999"),
	
	CENTROCOSTO("99999999999999999999"),
	
	AUXILIAR("99999999999999999999"),
	
	REFERENCIA("99999999999999999999"),
	
	FUENTERECURSO("99999999999999999999"),
	
	SUCURSAL001("001"),
	;
	
	
	/**
	 * Variable de acceso al código del auxiliar que se desea consultar
	 */
	private String value; 
	
	/**
	 * Constructor del enumerado
	 * 
	 * @param value
	 */
	private EnumAuxiliaresVarios(String value) {
		this.value = value;
	}
	/**
	 * 
	 * @return Valor de la contante que representa el auxiliar VARIOS
	 */
	public String getValue() {
		return value;
	}

	
}
