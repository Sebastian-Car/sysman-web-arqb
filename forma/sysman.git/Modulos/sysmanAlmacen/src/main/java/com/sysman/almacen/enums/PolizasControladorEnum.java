/*
 * PolizasControladorEnum
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
public enum PolizasControladorEnum {

    NOMBREASEGURADORA("NOMBREASEGURADORA"),

    ACTUALIZADA("ACTUALIZADA"),

    SEL_GRUPO("SEL_GRUPO"),

    SEL_SERIE("SEL_SERIE"),

    SEL_ELEMENTO("SEL_ELEMENTO"),

    VIGENTE("VIGENTE"),

    FECHAF("FECHAF"),

    FECHAI("FECHAI"),

    DIGITOS("DIGITOS"),

    GRUPO("GRUPO"),

    NUMERO_POLIZA("NUMERO_POLIZA"),

    ASEGURADORA("ASEGURADORA");

    private final String value;

    private PolizasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
