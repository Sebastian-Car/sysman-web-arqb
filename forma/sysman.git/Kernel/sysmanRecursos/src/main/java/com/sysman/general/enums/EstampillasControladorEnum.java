/*
* CuentasControladorEnum
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
public enum EstampillasControladorEnum {
    
    PARAM0("NUMERO"),  
    PARAM1("CLASEORDEN"),
    TIPOCONTRATO("TIPOCONTRATO"),
    NUMCONTRATO("NUMEROCONTRATO"),
    NIT("NIT"),
    VPROCULTURA("VALOR_PROCULTURA"),
    VPROADULTO("VALOR_PROADULTOMAYOR"),
    VPRODEPORTE("VALOR_PRODEPORTE");
    
                
    private final String value;

    private EstampillasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
