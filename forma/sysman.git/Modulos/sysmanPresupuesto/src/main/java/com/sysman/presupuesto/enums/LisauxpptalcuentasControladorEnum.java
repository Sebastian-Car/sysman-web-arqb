/*
* LisauxpptalcuentasControladorEnum
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
public enum LisauxpptalcuentasControladorEnum {
   
                PARAM15("CENTROINICIAL"),  
                  PARAM14("PARAM14"),  
                  PARAM3("PARAM3"),  
                  PARAM4("PARAM4"),  
                  PARAM1("PARAM1"),  
                  PARAM2("CODIGOINICIAL"),  
                  PARAM0("PARAM0"),  
                  PARAM9("PARAM9"),  
                  PARAM12("PARAM12"),  
                  PARAM7("CUENTAINICIAL"),  
                  PARAM13("PARAM13"),  
                  PARAM8("PARAM8"),  
                  PARAM10("TERCEROINICIAL"),  
                  PARAM5("PARAM5"),  
                  PARAM11("PARAM11"),  
                  PARAM6("PARAM6");
        	
	private final String value;
	
	private  LisauxpptalcuentasControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
