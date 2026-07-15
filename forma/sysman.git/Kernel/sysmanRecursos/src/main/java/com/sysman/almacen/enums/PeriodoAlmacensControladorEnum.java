/*
* PeriodoAlmacensControladorEnum
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
public enum PeriodoAlmacensControladorEnum {
   
                PARAM1("TIPOELEMENTO"),  
                  PARAM2("MES"),  
                  PARAM3("CLASEMOVIMIENTO"), 
                  PARAM4("CONCEPTOMOVIMIENTO"), 
                  PARAM5("CLASEBODEGAORIGEN"), 
                  PARAM6("CLASEBODEGADESTINO"), 
                  PARAM0("CLASEMOVIMIENTO");
        	
	private final String value;
	
	private  PeriodoAlmacensControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
