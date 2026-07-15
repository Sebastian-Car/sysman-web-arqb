/*
 * EstratosfgControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum NatSubComisionesControladorEnum {

    CO_FECHHASTA("CO_FECHHASTA"),

    CO_NUMEDOCU("CO_NUMEDOCU"),

    SUCURSAL("SUCURSAL"),

    KEY_SUCURSAL("KEY_SUCURSAL"),

    KEY_NUMERO_DCTO("KEY_NUMERO_DCTO");

    private final String value;

    private NatSubComisionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
