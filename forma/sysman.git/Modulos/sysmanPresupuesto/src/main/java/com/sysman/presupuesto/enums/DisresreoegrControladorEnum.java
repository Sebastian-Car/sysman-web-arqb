/*
 * DisresreoegrControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum DisresreoegrControladorEnum {

    PARAM2("TIPO"),
    PARAM0("CLASE"),
    PARAM6("NUMEROINICIAL"),
    CODIGOFINAL("CODIGOFINAL"),
    REFERENCIAINICIAL("REFERENCIAINICIAL"),
    FUENTEINICIAL("FUENTEINICIAL");

    private final String value;

    private DisresreoegrControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
