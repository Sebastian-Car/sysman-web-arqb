/*
 * PlantillaswordsControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum PlantillaswordsControladorEnum {

    /**
     * parametro ETIQUETAS
     */
    ETIQUETAS("ETIQUETAS"),
    /**
     * parametro ETIQUETA
     */
    ETIQUETA("ETIQUETA"),
    /**
     * parametro TIPO
     */
    TIPO("TIPO"),
    /**
     * parametro FECHA
     */
    FECHA("FECHA"),
    /**
     * parametro PLANTILLA
     */
    PLANTILLA("PLANTILLA"),
    /**
     * parametro MODULO
     */
    MODULO("MODULO");

    private final String value;

    private PlantillaswordsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}