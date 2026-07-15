/*
 * PrerequisitosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum PrerequisitosControladorEnum {

    VALOR_DOS("VALOR2"),

    VALOR_UNO("VALOR1"),

    CLASENOMBRE("CLASENOMBRE"),

    NOMBRETIPO("NOMBRETIPO"),

    SHARP_CAMPO_SHARP("#CAMPO#");

    private final String value;

    private PrerequisitosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
