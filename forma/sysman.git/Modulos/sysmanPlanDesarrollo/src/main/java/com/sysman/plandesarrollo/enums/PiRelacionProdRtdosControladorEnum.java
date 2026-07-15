/*
 * PiRelacionProdRtdosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.plandesarrollo.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum PiRelacionProdRtdosControladorEnum {

    CODIGO_PRODUCTO("CODIGO_PRODUCTO"),

    ID_PLAN_PRODUCTO("ID_PLAN_PRODUCTO"),

    ANIO("ANIO"),

    DIGMETAPROD("DIGMETAPROD");

    private final String value;

    private PiRelacionProdRtdosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
