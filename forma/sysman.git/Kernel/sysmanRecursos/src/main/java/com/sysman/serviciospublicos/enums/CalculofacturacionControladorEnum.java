/*
* CalculofacturacionControladorEnum
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
public enum CalculofacturacionControladorEnum {
   
    PARAM0("PARAM0"),
    CICLO("CICLO"),
    ANIO("ANO"),
    PER("PERIODO");
        	
	private final String value;
	
	private  CalculofacturacionControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
