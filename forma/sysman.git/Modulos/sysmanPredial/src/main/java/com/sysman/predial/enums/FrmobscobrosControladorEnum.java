/*-
 * FrmobscobrosControladorEnum.java
 *
 * 1.0
 * 
 * 5/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmobscobrosControladorEnum {

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_CODIGO("KEY_CODIGO"),

    KEY_NUMERO_ORDEN("KEY_NUMERO_ORDEN"),

    OBSERVACION_COBRO_COACTIVO("OBSERVACION_COBRO_COACTIVO");

    private final String value;

    private FrmobscobrosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
