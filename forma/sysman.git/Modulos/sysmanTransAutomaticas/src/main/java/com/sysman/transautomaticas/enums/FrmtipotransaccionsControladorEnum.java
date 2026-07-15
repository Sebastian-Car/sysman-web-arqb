/*
 * FrmprogramacionfinancieraControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.transautomaticas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmtipotransaccionsControladorEnum {

    PORCLASE("PORCLASE"),

    RUTA_ARCHIVO("RUTA_ARCHIVO");

    private final String value;

    private FrmtipotransaccionsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
