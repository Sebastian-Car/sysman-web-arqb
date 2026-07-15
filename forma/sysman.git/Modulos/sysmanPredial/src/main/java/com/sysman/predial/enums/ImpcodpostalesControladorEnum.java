package com.sysman.predial.enums;
/*-
 * ImpcodpostalesControladorEnum.java
 *
 * 1.0
 * 
 * 6/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ImpcodpostalesControladorEnum {

    CODIGO_POSTAL("CODIGO_POSTAL");

    private final String value;

    private ImpcodpostalesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
