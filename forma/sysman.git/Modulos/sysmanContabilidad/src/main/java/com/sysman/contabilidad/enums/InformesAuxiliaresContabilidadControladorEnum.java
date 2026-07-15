/*
* InformesAuxiliaresContabilidadControladorEnum
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
public enum InformesAuxiliaresContabilidadControladorEnum {
   
	PARAM0("MESINICIAL"),  

	PARAM1("TIPOINICIAL"),  

	PARAM2("CODIGOINICIAL"),  

	PARAM3("CENTRO_COSTO"),  
    
	PARAM4("AUXILIARINICIAL"),  
            
	PARAM5("TERCEROINICIAL"),
	
	PARAM6("MESFINAL"),
	
	PARAM7("DIA");
        	
	private final String value;
	
	private  InformesAuxiliaresContabilidadControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
