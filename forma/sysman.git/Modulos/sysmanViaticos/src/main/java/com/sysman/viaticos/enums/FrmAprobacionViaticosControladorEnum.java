/*
* FrmAprobacionViaticosControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum FrmAprobacionViaticosControladorEnum {
   
                TABLA("VI_VIATICOS"),
                PARAM1("TIPO"),
                PARAM0("FECHACONSULTA");
        	
	private final String value;
	
	private  FrmAprobacionViaticosControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
