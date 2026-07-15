/*
 * ExistenciadevxdepccControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmdocasociadomovimientosControladorEnum {

    PARAM3("NUMERODOCASOCIADO"),

    CLASE("CLASE"),

    DOCASOCIADO("DOCASOCIADO"),

    CLASEASOCIADO("CLASEASOCIADO"),

    TABLA("TEMP_D_MOVIMIENTO"),

    TIPOELEMENTO("TIPOELEMENTO"),
    
    FUENTER("FUENTER"),
    
    BODEGA("BODEGA");

    private final String value;

    private FrmdocasociadomovimientosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
