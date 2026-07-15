/*
 * ProyeccionesControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum ProyeccionesControladorEnum {


    PARAM0("TIPOCLASE");

    private final String value;

    private  ProyeccionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
