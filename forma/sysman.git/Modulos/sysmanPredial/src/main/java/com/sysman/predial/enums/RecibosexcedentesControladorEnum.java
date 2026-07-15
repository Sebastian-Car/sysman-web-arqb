/*
 * RecibosexcedentesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum RecibosexcedentesControladorEnum {


    PARAM4("CODIGOPREDIO"),  
    PARAM3("NUMEROFACTURA"),  
    PARAM1("NUMERO_FACTURA"),  
    PARAM2("DOCNUM"),  
    PARAM0("TIPO");

    private final String value;

    private  RecibosexcedentesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
