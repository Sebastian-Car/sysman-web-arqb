/*
* TraerPartidasConciliatoriasControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum TraerPartidasConciliatoriasControladorEnum {
   
	CODCUENTA("CODCUENTA"),  
    MES("MES"),
    MES_D("MES_D"),
    ANO_D("ANO_D");
        	
	private final String value;
	
	private  TraerPartidasConciliatoriasControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
