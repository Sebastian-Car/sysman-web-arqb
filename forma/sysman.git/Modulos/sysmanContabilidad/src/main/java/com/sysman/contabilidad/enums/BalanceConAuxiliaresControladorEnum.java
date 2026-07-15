/*
* BalanceConAuxiliaresControladorEnum
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
public enum BalanceConAuxiliaresControladorEnum {
   
                PARAM17("PARAM17"),  
                  PARAM16("PARAM16"),  
                  PARAM15("PARAM15"),  
                  PARAM14("PARAM14"),  
                  PARAM19("PARAM19"),  
                  PARAM18("PARAM18"),  
                  PARAM3("PARAM3"),  
                  PARAM4("PARAM4"),  
                  PARAM1("PARAM1"),  
                  PARAM2("PARAM2"),  
                  PARAM0("PARAM0"),  
                  PARAM9("PARAM9"),  
                  PARAM20("PARAM20"),  
                  PARAM7("PARAM7"),  
                  PARAM12("PARAM12"),  
                  PARAM21("PARAM21"),  
                  PARAM8("PARAM8"),  
                  PARAM13("PARAM13"),  
                  PARAM10("PARAM10"),  
                  PARAM5("PARAM5"),  
                  PARAM11("PARAM11"),  
                  PARAM6("PARAM6");
        	
    private final String value;
	
    private  BalanceConAuxiliaresControladorEnum(String value) {
        this.value = value;
    }
	
    public String getValue() {
        return value;
    }
}
