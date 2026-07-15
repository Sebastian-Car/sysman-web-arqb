/*
 * FrmfraudesControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmfraudesControladorEnum {

    PARAM5("PERIODOCIERREP"),
    
    PARAM4("ANOCIERREP"),
    
    PARAM3("FECHAGENERACION"),

    PARAM1("TIPO"),

    PARAM2("SUBCLASE"),

    PARAM0("FRAUDE");

    private final String value;

    private FrmfraudesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
