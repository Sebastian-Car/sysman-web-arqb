/*
* ListaCentroCostoControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum ListaCentroCostoControladorEnum {
   
                  PARAM2("PARAM2"),  
                  PARAM1("PARAM1"),  
                  PARAM0("PARAM0"),
                  COSTOINICIAL("COSTOINICIAL"),
                  MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),
                  NOMBREINFORME("000564ICentroCosto"),
                  MSM_INFORME_NO_EXISTE("MSM_INFORME_NO_EXISTE");
        	
	private final String value;
	
	private  ListaCentroCostoControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
