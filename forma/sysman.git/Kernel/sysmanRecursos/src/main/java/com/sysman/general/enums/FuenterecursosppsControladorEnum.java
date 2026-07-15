/*
 * FuenterecursosppsControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FuenterecursosppsControladorEnum {

    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("ANOFUENTE"),
    
	CODIGO_CLEOPATRA("CODIGO_CLEOPATRA");


    private final String value;

    private FuenterecursosppsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
