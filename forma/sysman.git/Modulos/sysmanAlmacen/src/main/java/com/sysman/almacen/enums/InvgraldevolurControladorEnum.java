/*
* InvgraldevolurControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
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
public enum InvgraldevolurControladorEnum {
   
                PARAM3("PARAM3"),  
                  PARAM4("PARAM4"),  
                  PARAM1("ELEMENTODESDE"),  
                  PARAM2("TERCEROINICIAL"),  
                  PARAM0("TIPOELEMENTO"),  
                  PARAM5("PARAM5");
        	
	private final String value;
	
	private  InvgraldevolurControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
