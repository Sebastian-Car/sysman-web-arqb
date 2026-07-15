/*
 * MediosMagneticosDIANControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum MediosMagneticosDIANControladorEnum {

    ID_PROCESO("ID_PROCESO"),

    PROCESO("PROCESO"),

    IDPROCESO("IDPROCESO"),

    PARAM2("PARAM2"),

    PARAM0("PARAM0"),

    PARAM9("PARAM9"),

    PARAM7("PARAM7"),

    PARAM8("PARAM8"),

    PARAM5("PARAM5"),

    PARAM6("PARAM6"),

    NOMBRES("NOMBRES");

    private final String value;

    private MediosMagneticosDIANControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
