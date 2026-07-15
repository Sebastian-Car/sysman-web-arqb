/*
* RevisainterfacefactsControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum RevisainterfacefactsControladorEnum {
   
                  PARAM3("PARAM3"),  
                  PARAM4("PARAM4"),  
                  PARAM1("PARAM1"),  
                  PARAM2("PARAM2"),  
                  PARAM0("PARAM0"),  
                  PARAM7("PARAM7"),  
                  PARAM5("PARAM5"),  
                  PARAM6("PARAM6");
        	
	private final String value;
	
	private  RevisainterfacefactsControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
