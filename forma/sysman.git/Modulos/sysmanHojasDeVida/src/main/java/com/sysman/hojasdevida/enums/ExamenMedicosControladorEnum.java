/*
 * ExamenMedicosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ExamenMedicosControladorEnum {

    NOMBRE_ENFERMEDAD("NOMBRE_ENFERMEDAD"),

    NOMBRETIPOEXAMEN("NOMBRETIPOEXAMEN"),

    NOMBRES("NOMBRES"),

    FECHA_APARICION("FECHA_APARICION"),

    CODIGOEN("CODIGOEN");

    private final String value;

    private ExamenMedicosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
