/*
 * ActualizarubicacionControladorEnum
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
public enum ActualizarubicacionControladorEnum {
    TB_TB3157("TB_TB3157"),
    CODIGOFINAL("CODIGOFINAL"),
    
    CODIGOINICIAL("CODIGOINICIAL");

    private final String value;

    private  ActualizarubicacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
