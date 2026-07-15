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
public enum FrmCaEjecucionPasControladorEnum {

    PARAM0("CODIGOINICIAL");

    private final String value;

    private  FrmCaEjecucionPasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}