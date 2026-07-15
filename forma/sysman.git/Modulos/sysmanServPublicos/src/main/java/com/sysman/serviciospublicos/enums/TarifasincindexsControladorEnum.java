/*
 * TarifasincindexsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum TarifasincindexsControladorEnum {

    PARAM3("INDALUM"), 
    PARAM1("INDACUEDUCTO"),  
    PARAM2("INDASEO"),  
    PARAM0("INDALCAN");

    private final String value;

    private  TarifasincindexsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
