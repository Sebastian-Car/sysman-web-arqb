/*
 * FrmviasControladorEnum
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
public enum FrmviasControladorEnum {

    ID_VIA("ID_VIA"),

    TIPO("TIPO"),

    SERIE_PLACA("SERIE_PLACA"),

    TIPOELEMENTO("TIPOELEMENTO"),

    ACABADO("ACABADO"),

    LONGITUD("LONGITUD"),

    ANCHO("ANCHO"),

    VIDA_UTIL("VIDA_UTIL"),

    CODIGO_ITEM("CODIGO_ITEM");

    private final String value;

    private FrmviasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
