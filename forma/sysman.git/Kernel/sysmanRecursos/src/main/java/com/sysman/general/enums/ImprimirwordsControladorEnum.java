/*-
 * ImprimirwordsControladorEnum.java
 *
 * 1.0
 * 
 * 5/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 5/04/2017
 * @author amonroy
 *
 */
public enum ImprimirwordsControladorEnum {
    /**
     * parametro CODIGOPLANTILLA
     */
    CODIGOPLANTILLA("CODIGOPLANTILLA"),

    /**
     * parametro FECHAPLANTILLA
     */
    FECHAPLANTILLA("FECHAPLANTILLA"),

    /**
     * parametro MODELO_VARIABLES
     */
    MODELO_VARIABLES("MODELO_VARIABLES"),

    /**
     * parametro PLANTILLA
     */
    PLANTILLA("PLANTILLA");

    private final String value;

    private ImprimirwordsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
