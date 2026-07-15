/*
* SubformmovpptalsControladorEnum
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
public enum SubformmovpptalsControladorEnum {
   
                PARAM3("NOMBRE"),  
                  PARAM4("TCOMPONENTE"),  
                  PARAM1("MESFINAL"),  
                  PARAM2("NATURALEZA"),  
                  PARAM0("MESINICIAL"),  
                  PARAM9("MESINICIAL"),  
                  PARAM12("PARAM12"),  
                  PARAM7("PARAM7"),  
                  PARAM13("PARAM13"),  
                  PARAM8("PARAM8"),  
                  PARAM5("PARAM5"),  
                  PARAM10("PARAM10"),  
                  PARAM11("PARAM11"),  
                  PARAM6("PARAM6");
        	
	private final String value;
	
	private  SubformmovpptalsControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
