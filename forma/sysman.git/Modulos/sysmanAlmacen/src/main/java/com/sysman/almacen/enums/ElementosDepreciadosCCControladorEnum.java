package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum ElementosDepreciadosCCControladorEnum {
   
	elementoInicial("elementoInicial"),
	elementoFinal("elementoFinal");
        	
	private final String value;
	
	private  ElementosDepreciadosCCControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
