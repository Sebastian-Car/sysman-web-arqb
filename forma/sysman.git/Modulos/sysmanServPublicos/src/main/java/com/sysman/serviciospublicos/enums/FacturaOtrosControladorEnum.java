/*
* FacturaOtrosControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum FacturaOtrosControladorEnum {
   
    TABLAUSUHIST("SP_USUARIO_HISTORICOS"),            
    TABLAMEDIDOR("SP_MEDIDOR");
        	
	private final String value;
	
	private  FacturaOtrosControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
