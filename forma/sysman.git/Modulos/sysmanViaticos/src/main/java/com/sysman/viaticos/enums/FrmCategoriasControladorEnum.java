/*
 * FrmCategoriasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmCategoriasControladorEnum {

    ESCALAFON("ESCALAFON"),

    ID_DE_CATEGORIA("ID_DE_CATEGORIA");

    private final String value;

    private FrmCategoriasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
