/*
 * BalanceAuxiliarControladorEnum
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
public enum BalanceAuxiliarControladorEnum {
    ANIO("ANIO"),
    PARAM9("PARAM9"),  
    PARAM6("268px"),  
    PARAM5("none"),  
    PARAM8("PARAM8"),  
    CODIGOFINAL("CODIGOFINAL"),
    SYSDATE("SYSDATE"),  
    PARAM2("255px"),  
    PARAM1("block"),  
    PARAM4("320px"),  
    PARAM3("360px"),
    NIVEL("NIVEL"),
    CODIGOINICIAL("CODIGOINICIAL"),        
    PARAM0("230px");

    private final String value;

    private  BalanceAuxiliarControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
