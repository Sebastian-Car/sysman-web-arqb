/*
* LibroMayoryBalancesControladorEnum
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
public enum LibroMayoryBalancesControladorEnum {
   
                CINICIAL("CUENTAINICIAL"),  
                  COSTOIN("COSTOINICIAL"),  
                  COD("CODIGO"),  
                  SYSDAT("SYSDATE"),  
                  PARAM4("PARAM4"),  
                  PARAM3("PARAM3"),  
                  PARAM0("PARAM0"),
                  MSJ1("TB_TB133"),
                  MSJ2("TB_TB134");
    
        	
	private final String value;
	
	private  LibroMayoryBalancesControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
