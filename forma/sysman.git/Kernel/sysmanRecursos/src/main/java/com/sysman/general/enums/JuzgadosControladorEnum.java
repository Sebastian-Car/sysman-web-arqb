/*
 * JuzgadosControladorEnum
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
 * EnumeraciÃ³n que permite clasificar cada uno de los parÃ¡metros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeraciÃ³n.
 */
public enum JuzgadosControladorEnum {

    DEPARTAMENTO("DEPARTAMENTO"),

    NOMBREPAIS("NOMBREPAIS"),

    NOMBREDEPARTAMENTO("NOMBREDEPARTAMENTO"),

    NOMBRECIUDAD("NOMBRECIUDAD"),

    PAIS("PAIS");

    private final String value;

    private JuzgadosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
