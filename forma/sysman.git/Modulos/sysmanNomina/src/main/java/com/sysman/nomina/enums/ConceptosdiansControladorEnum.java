/*-
 * ActualizaparametrosretroactivosControladorEnum.java
 *
 * 1.0
 *
 * 18/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 *
 * @version 1.0, 11/02/2019
 * @author mzanguna
 *
 */
public enum ConceptosdiansControladorEnum {

    NOMBRETIPOPARAMETRO("NOMBRETIPOPARAMETRO"),

    TIPOPARAMETRO("TIPOPARAMETRO"),

    MODULO("MODULO"),

    ANIO("ANIO"),

    NOMBRE_CONCEPTO("NOMBRE_CONCEPTO");

    private final String value;

    private ConceptosdiansControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
