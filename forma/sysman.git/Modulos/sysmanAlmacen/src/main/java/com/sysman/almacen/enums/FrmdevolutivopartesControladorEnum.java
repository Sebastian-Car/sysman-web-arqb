/*
 * FrmdevolutivopartesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmdevolutivopartesControladorEnum {

    ELEMENTO_PADRE("ELEMENTO_PADRE"),

    SERIE_PADRE("SERIE_PADRE");

    private final String value;

    private FrmdevolutivopartesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
