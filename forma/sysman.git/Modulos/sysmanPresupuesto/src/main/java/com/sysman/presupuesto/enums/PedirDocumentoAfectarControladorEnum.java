/*
* PedirDocumentoAfectarControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum PedirDocumentoAfectarControladorEnum {
   
	
	TIPOAF("TIPOAF"),  
	NUMEROAFE("NUMEROAFE"),  
	SUCURSAL("SUCURSAL"),  
	TERCERO("TERCERO"),  
                INDAFECTACION("INDAFECTACION"),  
                  PIDETERCERO("PIDETERCERO"),  
                  FECHACOMP("FECHACOMP"),  
                  FECHA("FECHA	"),  
                  TIPO("TIPO");
        	
	private final String value;
	
	private  PedirDocumentoAfectarControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
