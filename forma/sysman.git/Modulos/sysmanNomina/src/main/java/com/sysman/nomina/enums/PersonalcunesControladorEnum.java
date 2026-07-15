/*
 * PersonalsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos
 * Map<String,String> y disponibles en dicha enumeración.
 */
public enum PersonalcunesControladorEnum {

    BANCO("BANCO"),

    IDEMPLEADO("ID_DE_EMPLEADO"),

    TIPOVINCULACIONNIE("TIPOVINCULACIONNIE"),

    TCONTRATO_NIE061("TCONTRATO_NIE061"),
    
    STIPO_NIE042("STIPO_NIE042"),
    
    CUENTA("CUENTA"),

    TIPOCUENTA("TIPOCUENTA"),

    MEDIOPAGONIE("MEDIOPAGONIE")    
    ;

    private final String value;

    private PersonalcunesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
