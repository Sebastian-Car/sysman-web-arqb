/*
 * FrmestproypastosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmestproypastosControladorEnum {

    COD_PROY("COD_PROY"),

    SECTORNUEVO("SECTORNUEVO"),

    NOMBREPROYECTO("NOMBREPROYECTO"),

    DESCRIPCIONNUEVA("DESCRIPCIONNUEVA"),

    COMPONENTENOMBRE("COMPONENTENOMBRE"),

    CANTIDAD_PLAN("CANTIDAD_PLAN"),

    TIPOT("TIPOT"),

    T_COMPONENTE("T_COMPONENTE"),

    NOMBRE_FUENTE("NOMBRE_FUENTE");

    private final String value;

    private FrmestproypastosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
